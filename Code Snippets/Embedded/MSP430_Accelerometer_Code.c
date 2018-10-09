//eUSCI_B0 is used for I2C
//Have to use Timer_B0 for this?
//Might have to use P1.6 for Data - SDL
//Might have to use P1.7 for Clock - SCL

//Output is 12 bit, but we can ignore LSB and just use MSB for 8 bit
//This is from the datasheet

#include "msp430fr5969.h"
#include <stdio.h>

int main(void){

	//Configure the pins for the Accelerometer
	init_I2C();

}

void init_I2C(){

	//Initialize the Data(SDL) and Clock(SCL) Pins
	P1SEL0 &= ~(BIT6 | BIT7);
	P1SEL1 |= (BIT6 | BIT7);

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
	//Set UCTXNACK - 
	//Set UCTXSTP
	//Set UCTXSTT
	//Set UCSWRST to 1 - Set the reset to start
	UCB0CTLW0 |= 

	//Setup CTLW1

	//Setup STAT Register
	
}