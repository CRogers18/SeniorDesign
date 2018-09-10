/*
 * MSP430G2253 USCI-A UART code
 * Anthony Scranney
 * www.Coder-Tronics.com
 * October 2014
 *
 * The code can be used to interface with the HC06 Bluetooth adpator board.
 * The code waits for data to be received which then calls the UART interrupt,
 * the received buffer is then assigned to the variable Rx_Data.  Rx_Data is
 * used in a switch case statment to action various functions depending on the
 * ASCII code received over Bluetooth.
 */

#include <msp430g2253.h>

unsigned char Rx_Data = 0;					// Byte received via UART
unsigned int count = 0;						// Used for the flashing LED demonstration

int main(void)
{
	/*** Set-up system clocks ***/
	WDTCTL = WDTPW + WDTHOLD;				// Stop WDT
	if (CALBC1_1MHZ == 0xFF)				// If calibration constant erased
		while (1);						// do not load, trap CPU!
	
	DCOCTL = 0;								// Select lowest DCOx and MODx settings
	BCSCTL1 = CALBC1_1MHZ;					// Set DCO
	DCOCTL = CALDCO_1MHZ;
	/*** Set-up GPIO ***/
	P1SEL = BIT1 + BIT2;					// P1.1 = RXD, P1.2=TXD
	P1SEL2 = BIT1 + BIT2;					// P1.1 = RXD, P1.2=TXD
	P1DIR |= BIT0 + BIT3 + BIT4 +
			BIT5 + BIT6 + BIT7;				// Pins set as outputs
	P1OUT &= ~(BIT0 + BIT3 + BIT4 +
			BIT5 + BIT6 + BIT7);			// Set all pins low
	
	/*** Set-up USCI A ***/
	UCA0CTL1 |= UCSSEL_2;					// SMCLK
	UCA0BR0 = 104;							// 1MHz 9600
	UCA0BR1 = 0;							// 1MHz 9600
	UCA0MCTL = UCBRS0;						// Modulation UCBRSx = 1
	UCA0CTL1 &= ~UCSWRST;					// Initialize USCI state machine
	IE2 |= UCA0RXIE;						// Enable USCI_A0 RX interrupt
	__bis_SR_register(LPM0_bits + GIE);		// Enter LPM0, interrupts enabled

  while(1)
  {
		switch (Rx_Data)
		{
			case 0x41:							// Forward Command 0001
				while (!(IFG2 & UCA0TXIFG));	// USCI_A0 TX buffer ready?
				UCA0TXBUF = 0x41;				// Send 8-bit character
			    TA0CCTL0 &= ~CCIE;				// Disable Timer0_A interrupts
			   // Select P1.0 and set  High and set P1.4 P1.5 and P1.7 to low
			    P1SEL &= ~(BIT0 + BIT4 + BIT5 + BIT6); // PINS Selected as GPIO
			    P1OUT &= ~(BIT0 + BIT4 + BIT5 + BIT6); // Turn all off
			    P1OUT |= BIT0; 						  //Set P1.0 to high
				break;

			case 0x42:							// Backward Command 0010
				while (!(IFG2&UCA0TXIFG));	// USCI_A0 TX buffer ready?
				UCA0TXBUF = 0x42;				// Send 8-bit character
			    TA0CCTL0 &= ~CCIE;				// Disable Timer0_A interrupts
			  // Select P1.6 and set  High and set P1.3 P1.5 and P1.7 to low
			   P1SEL &= ~(BIT0 + BIT4 + BIT5 + BIT6); // PINS Selected as GPIO
		       P1OUT &= ~(BIT0 + BIT4 + BIT5 + BIT6); // Turn all off
			   P1OUT |= BIT6; 					     //Set P1.6 to high
				break;

			case 0x43:							// Left Command 00
				while (!(IFG2&UCA0TXIFG));	// USCI_A0 TX buffer ready?
				UCA0TXBUF = 0x43;             // Send 8-bit character
			    TA0CCTL0 &= ~CCIE;				// Disable Timer0_A interrupts
		  // Select P1.4 and set  High and set P1.3 P1.5 and P1.7 to low
			   P1SEL &= ~(BIT0 + BIT4 + BIT5 + BIT6); // PINS Selected as GPIO
		       P1OUT &= ~(BIT0 + BIT4 + BIT5 + BIT6); // Turn all off
			   P1OUT |= BIT4; 					     //Set P1.4 to high
			break;

			case 0x44:							// Right Command
				while (!(IFG2&UCA0TXIFG));	// USCI_A0 TX buffer ready?
			  	   UCA0TXBUF = 0x44;				// Send 8-bit character
			  TA0CCTL0 &= ~CCIE;				// Disable Timer0_A interrupts
			 // Select P1.5 and set  High and set P1.4 P1.5 and P1.3 to low
			 P1SEL &= ~(BIT0 + BIT4 + BIT5 + BIT6); // PINS Selected as GPIO
		     P1OUT &= ~(BIT0 + BIT4 + BIT5 + BIT6); // Turn all off
			 P1OUT |= BIT5; 				     //Set P1.5 to high
			  break;

			case 0x3C:							// Command
				break;

			case 0x31:							// 1 Command
				break;

			case 0x32:							// 2 Command
				break;

			case 0x33:							// 3 Command
				break;

			case 0x34:							// 4 Command
			break;

			case 0x35:							// 5 Command
				break;

			default: break;
		}
		 __bis_SR_register(LPM0_bits);	// Enter LPM0, interrupts enabled
  }
}

//  USCI A interrupt handler
#if defined(__TI_COMPILER_VERSION__) || defined(__IAR_SYSTEMS_ICC__)
#pragma vector=USCIAB0RX_VECTOR
__interrupt void USCI0RX_ISR(void)
#elif defined(__GNUC__)
void __attribute__ ((interrupt(USCIAB0RX_VECTOR))) USCI0RX_ISR (void)
#else
#error Compiler not supported!
#endif
{
	Rx_Data = UCA0RXBUF;					// Assign received byte to Rx_Data
	__bic_SR_register_on_exit(LPM0_bits);	// Wake-up CPU
}

#pragma vector=TIMER0_A0_VECTOR     // Timer0 A0 interrupt service routine
   __interrupt void Timer0_A0 (void) {

	   count++;
	   if (count == 10)
		   {
		   P1OUT ^= BIT0 + BIT6;					// P1.0 Toggle (Red LED)
		   count =0;
		   }
}