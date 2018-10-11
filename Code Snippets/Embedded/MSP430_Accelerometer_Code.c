//eUSCI_B0 is used for I2C
//Have to use Timer_B0 for this?
//Might have to use P1.6 for Data - SDL
//Might have to use P1.7 for Clock - SCL

//Output is 12 bit, but we can ignore LSB and just use MSB for 8 bit
//This is from the datasheet

#include "msp430fr5969.h"
#include <stdio.h>

int main(void){

    WDTCTL = WDTPW | WDTHOLD;                 // Stop Watchdog

    // Startup clock system with max DCO setting ~8MHz
    CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
    CSCTL1 = DCOFSEL_3 | DCORSEL;             // Set DCO to 8MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
    CSCTL0_H = 0;                             // Lock CS registers

    //Configure the pins for the Accelerometer
    init_I2C();

    while (1)
    {
        while (UCB0CTLW0 & UCTXSTP);             // Ensure stop condition got sent
        UCB0CTLW0 |= UCTXSTT;                    // I2C start condition
        while (UCB0CTLW0 & UCTXSTT);             // Start condition sent?
        UCB0CTLW0 |= UCTXSTP;                    // I2C stop condition
        __bis_SR_register(LPM3);        // Enter LPM0 w/ interrupts

    }

}

void init_I2C(){

    printf("Begin\n");
    //Initialize the Data(SDL Pin6) and Clock(SCL Pin7) Pins
    P1SEL0 &= ~(BIT6 | BIT7);
    P1SEL1 |= (BIT6 | BIT7);

    //Enable UCSWRST to configure everything
    UCB0CTLW0 |= UCSWRST;

    //Setup CTLW0
    //Set UCA10 to 0 - 7 bit own addressing
    //Set UCSLA10 to 0 - 7 bit slave addressing
    //Set UCMM to 0 - Single master enviornment
    //Set UCMST to 1 - Master Mode for the Microcontroller
    //Set UCMODE0 to 11 - Uses I2C Mode
    //Set UCSYNC to 1
    //Set UCSSEL0 to 11 - SMCLK
    //Set UCTXACK to 1 - Acknowledge the slave address
    //Set UCTR to 0 - Reciever (Since Im getting info from the Accelerometer)
    //Set UCTXNACK to 0 - Regualr aknowledge
    //Set UCTXSTP to 0 - No STOP
    //Set UCTXSTT to 0 - No START
    //Dont mess with UCSWRST to 1
    UCB0CTLW0 |= UCMST | UCMODE_3 | UCSYNC | UCSSEL_2;// | UCTXACK;

    //Setup CTLW1
    //Might not need to do this

    UCB0BRW = 80; //SMCLK/80 = ~100KHz???

    //Slave Address - Do I need to declare this
    //Figure out the slave address
    //UCB0I2CSA = 0x1D;

    //Disable UCSWRST
    UCB0CTLW0 &= ~UCSWRST;

    //Enable interrupt
    UCB0IE |= UCRXIE1;
}

#pragma vector = USCI_I2C_UCRXIFG0
__interrupt void Accelerometer_Interrupt(void){
    printf("check");
    fflush(stdout);
}










