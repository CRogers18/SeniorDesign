//Embedded Systems Lab Experiment 2
void Init_UART(void);
void OUTA_UART(unsigned char A);
unsigned char INCHAR_UART(void);

#include "msp430fr5969.h"
#include "stdio.h"

void print_string(char string []){
    //This function prints the string char by char
    int size = strlen(string);
    int i = 0;
    char a;
    for(i = 0; i < size; i++)
    {
        //simply get the char from array and pass to OUTA
        a = string[i];
        OUTA_UART(a);
    }

    return;
}

int main(void){
    //This is the string that needs to be printed
    char string[] = {"Laboratory #2 for EEL4742 Embedded Systems"};
    volatile unsigned char a;
    volatile unsigned int i; // volatile to prevent optimization
    WDTCTL = WDTPW + WDTHOLD; // Stop watchdog timer
    Init_UART();
    //Call the print_string function to output to terminal
    //print_string(string);
    unsigned char A = 't';
    OUTA_UART(A);

}

void OUTA_UART(unsigned char A){
    // IFG2 register (1) = 1 transmit buffer is empty,
    // UCA0TXBUF 8 bit transmit buffer
    // wait for the transmit buffer to be empty before sending the
    // data out
  	do
    {
      
    } while((UCA0IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA0TXBUF_L = A;
}

unsigned char INCHAR_UART(void){
    // IFG2 register (0) = 1 receive buffer is full,
    // UCA0RXBUF 8 bit receive buffer
    // wait for the receive buffer is full before getting the data
    do
    {
      
    } while((UCA0IFG & 0x01) == 0);

        // go get the char from the receive buffer
    return (UCA0RXBUF_L);
}

void Init_UART(void){
    //The 8 bit selector is broken up into 2 registers
    P2SEL1 = 0x6; //2.5 and 2.6 are T and R respectively
    P2SEL0 = 0x0;
    ////////////////////////////////////////////////////////

    
    
    


    /////////////////////////////
    //UCA0MCTLW=0x06;
     // low frequency mode module 3 modulation pater
     // used for the bit clock
    UCA0STATW= 0x00; // do not loop the transmitter back to the
     // receiver for echoing
     // (7) = 1 echo back trans to rec
     // (6) = 1 framing, (5) = 1 overrun, (4) =1 Parity,
     // (3) = 1 break
     // (0) = 2 transmitting or receiving data
    ////////////////////////////////////////////////////////////



    UCA0CTLW0= 0x4040;
     // take UART out of reset
    //ADC12IER2 = 0; // turn transmit interrupts off
    //---------------------------------------------------------------
    //***************************************************************
    //---------------------------------------------------------------
     // IFG2 register (0) = 1 receiver buffer is full,
     // UCA0RXIFG
     // IFG2 register (1) = 1 transmit buffer is empty,
     // UCA0RXIFG
     // UCA0RXBUF 8 bit receiver buffer
     // UCA0TXBUF 8 bit transmit buffer
}

// UCAxCTLW0
0100 0000 0100 0000 = 0x4040
  