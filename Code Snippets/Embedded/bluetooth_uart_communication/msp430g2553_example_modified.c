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

//Modified for the MSP430FR5969
void OUTA_UART(char A);
#include "msp430.h"

unsigned char Rx_Data = 0;                  // Byte received via UART
unsigned int count = 0;                     // Used for the flashing LED demonstration

int main(void)
{
    /*** Set-up system clocks ***/
    WDTCTL = WDTPW + WDTHOLD;               // Stop WDT
//    if (CALBC1_1MHZ == 0xFF)                // If calibration constant erased
//        while (1);                      // do not load, trap CPU!

//    DCOCTL = 0;                             // Select lowest DCOx and MODx settings
//    BCSCTL1 = CALBC1_1MHZ;                  // Set DCO
//    DCOCTL = CALDCO_1MHZ;
    /*** Set-up GPIO ***/


    // Configure GPIO
    P2SEL1 |= BIT0 | BIT1;                    // USCI_A0 UART operation
    P2SEL0 &= ~(BIT0 | BIT1);


    //Init UART
    // Disable the GPIO power-on default high-impedance mode to activate
    // previously configured port settings
    PM5CTL0 &= ~LOCKLPM5;

    // Startup clock system with max DCO setting ~8MHz
    CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
    CSCTL1 = DCOFSEL_3 | DCORSEL;             // Set DCO to 8MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
    CSCTL0_H = 0;                             // Lock CS registers

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


    UCA0IE |= UCRXIE;                         // Enable USCI_A0 RX interrupt
    __bis_SR_register(LPM3_bits | GIE);       // Enter LPM3, interrupts enabled

  while(1)
  {
        switch (Rx_Data)
        {
            //default: break;
            default:                    //Set P1.0 to high
                OUTA_UART(Rx_Data);
                break;
        }
         __bis_SR_register(LPM0_bits);  // Enter LPM0, interrupts enabled
  }
}

void OUTA_UART(char A){
    while(!(UCA0IFG&UCTXIFG));    // USCI_A0 TX buffer ready?
        UCA0TXBUF = A;               // Send 8-bit character

        //TA0CCTL0 &= ~CCIE;              // Disable Timer0_A interrupts
}

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

// #pragma vector=TIMER0_A0_VECTOR     // Timer0 A0 interrupt service routine
//    __interrupt void Timer0_A0 (void) {

//        count++;
//        if (count == 10)
//            {
//            P1OUT ^= BIT0 + BIT6;                    // P1.0 Toggle (Red LED)
//            count =0;
//            }
// }
