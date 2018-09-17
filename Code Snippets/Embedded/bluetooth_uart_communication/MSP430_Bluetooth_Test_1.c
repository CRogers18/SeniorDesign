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
void init_UART();
void OUTA_UART(unsigned char data);
unsigned char INCHAR_UART(void);

#include "msp430fr5969.h"

unsigned char Rx_Data = 0;                  // Byte received via UART
unsigned int count = 0;                     // Used for the flashing LED demonstration

int main(void)
{
    // Configure GPIO
        P2SEL1 |= BIT5 | BIT6;                    // USCI_A0 UART operation
        P2SEL0 &= ~(BIT5 | BIT6);

        // Disable the GPIO power-on default high-impedance mode to activate
        // previously configured port settings
        PM5CTL0 &= ~LOCKLPM5;

        // Startup clock system with max DCO setting ~8MHz
        CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
        CSCTL1 = DCOFSEL_3 | DCORSEL;             // Set DCO to 8MHz
        CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
        CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
        CSCTL0_H = 0;                             // Lock CS registers

    	init_UART();

    	__bis_SR_register(LPM0_bits + GIE);     // Enter LPM0, interrupts enabled

	      while(1)
	      {
	            switch (Rx_Data)
	            {
	                case 0x41:                          // Forward Command 0001
	                    OUTA_UART(0x41);              // Send 8-bit character
	                    TA0CCTL0 &= ~CCIE;              // Disable Timer0_A interrupts                    //Set P1.0 to high
	                    break;

	                default: break;
	            }
	         __bis_SR_register(LPM0_bits);  // Enter LPM0, interrupts enabled
	  }
}


//  USCI A interrupt handler
#if defined(__TI_COMPILER_VERSION__) || defined(__IAR_SYSTEMS_ICC__)
#pragma vector=USCI_A0_VECTOR
__interrupt void USCI_A0_ISR(void)
#elif defined(__GNUC__)
void __attribute__ ((interrupt(USCI_A0_VECTOR))) USCI_A0_ISR (void)
#else
#error Compiler not supported!
#endif
{
    Rx_Data = UCA0RXBUF;                    // Assign received byte to Rx_Data
    __bic_SR_register_on_exit(LPM0_bits);   // Wake-up CPU
}

void init_UART(){

    // Configure USCI_A0 for UART mode
    UCA0CTLW0 = UCSWRST;                      // Put eUSCI in reset
    UCA0CTLW0 |= UCSSEL__SMCLK;               // CLK = SMCLK
    // Baud Rate calculation
    // 8000000/(16*9600) = 52.083
    // Fractional portion = 0.083
    // User's Guide Table 21-4: UCBRSx = 0x04
    // UCBRFx = int ( (52.083-52)*16) = 1
    UCA0BR0 = 52;                             // 8000000/16/9600
    UCA0BR1 = 0x00;
    UCA0MCTLW |= UCOS16 | UCBRF_1;
    UCA0CTLW0 &= ~UCSWRST;                    // Initialize eUSCI
    UCA0IE |= UCRXIE;
}

void OUTA_UART(unsigned char data){

    // wait for the transmit buffer to be empty before sending data
    do{}
    while((UCA0IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA0TXBUF_L = data;
}

unsigned char INCHAR_UART(void){

    // wait for the receive buffer to be full before getting the data
    do{}
    while((UCA0IFG & 0x01) == 0);

    // go get the char from the receive buffer
    return UCA0RXBUF_L;
}
