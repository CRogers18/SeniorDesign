//This code is a combination of our code and examples given by TI
//Code has a init_uart, out to uart and recieve from uart


#include <msp430.h> 

//Taken from TIs example: msp430fr59xx_euscia0_uart_01.c
void init_UART()
{
    WDTCTL = WDTPW | WDTHOLD;                 // Stop Watchdog

      // Configure GPIO
      P2SEL1 |= BIT0 | BIT1;                    // USCI_A0 UART operation
      P2SEL0 &= ~(BIT0 | BIT1);

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
}

void OUTA_UART(unsigned char data)
{
    // wait for the transmit buffer to be empty before sending data
    do
    {

    } while((UCA0IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA0TXBUF_L = data;
}

unsigned char INCHAR_UART(void)
{
    // wait for the receive buffer to be full before getting the data
    do
    {

    } while((UCA0IFG & 0x01) == 0);

    // go get the char from the receive buffer
    return UCA0RXBUF_L;
}

/**
 * main.c
 */
int main(void)
{
    WDTCTL = WDTPW | WDTHOLD;   // stop watchdog timer

    init_UART();
    OUTA_UART('A');

    return 0;
}
