//eUSCI_B0 is used for I2C
//Have to use Timer_B0 for this?
//Might have to use P1.6 for Data - SDL
//Might have to use P1.7 for Clock - SCL

//Output is 12 bit, but we can ignore LSB and just use MSB for 8 bit
//This is from the datasheet

#include <msp430.h>
#include <stdio.h>

enum MMA8652_REGISTER {
    STATUS           = 0x00,
    OUT_X_MSB        = 0x01,
    OUT_X_LSB        = 0x02,
    OUT_Y_MSB        = 0x03,
    OUT_Y_LSB        = 0x04,
    OUT_Z_MSB        = 0x05,
    OUT_Z_LSB        = 0x06,
    SYSMOD           = 0x0B,
    INT_SOURCE       = 0x0C,
    WHO_AM_I         = 0x0D,
    XYZ_DATA_CFG     = 0x0E,
    HP_FILTER_CUTOFF = 0x0F,
    PL_STATUS        = 0x10,
    PL_CFG           = 0x11,
    PL_COUNT         = 0x12,
    PL_BF_ZCOMP      = 0x13,
    P_L_THS_REG      = 0x14,
    FF_MT_CFG        = 0x15,
    FF_MT_SRC        = 0x16,
    FF_MT_THS        = 0x17,
    FF_MT_COUNT      = 0x18,
    TRANSIENT_CFG    = 0x1D,
    TRANSIENT_SRC    = 0x1E,
    TRANSIENT_THS    = 0x1F,
    TRANSIENT_COUNT  = 0x20,
    PULSE_CFG        = 0x21,
    PULSE_SRC        = 0x22,
    PULSE_THSX       = 0x23,
    PULSE_THSY       = 0x24,
    PULSE_THSZ       = 0x25,
    PULSE_TMLT       = 0x26,
    PULSE_LTCY       = 0x27,
    PULSE_WIND       = 0x28,
    ASLP_COUNT       = 0x29,
    CTRL_REG1        = 0x2A,
    CTRL_REG2        = 0x2B,
    CTRL_REG3        = 0x2C,
    CTRL_REG4        = 0x2D,
    CTRL_REG5        = 0x2E,
    OFF_X            = 0x2F,
    OFF_Y            = 0x30,
    OFF_Z            = 0x31
};

enum SYSMOD {
    SYSMOD_STANDBY = 0x00,
    SYSMOD_WAKE    = 0x01,
    SYSMOD_SLEEP   = 0x02
};


enum INT_SOURCE_BIT {
  SRC_DRDY   = 0x01,    // Data-ready interrupt bit status.
  SRC_FF_MT  = 0x04,    // Freefall/motion interrupt status bit.
  SRC_PULSE  = 0x08,    // Pulse interrupt status bit.
  SRC_LNDPRT = 0x10,    // Landscape/portrait orientation interrupt status bit.
  SRC_TRANS  = 0x20,    // Transient interrupt status bit.
  SRC_ASLP   = 0x80,    // Auto-sleep/wake interrupt status bit.
};






int main(void){

    WDTCTL = WDTPW | WDTHOLD;                 // Stop Watchdog
    PM5CTL0 &= ~LOCKLPM5;                   // Disable the GPIO power-on default high-impedance mode
                                                // to activate previously configured port settings

    // Startup clock system with DCO 1MHz
    CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
    CSCTL1 = DCOFSEL_0 | DCORSEL;             // Set DCO to 1MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
    CSCTL0_H = 0;    // Lock CS registers

    //Configure the pins for the Accelerometer
    init_I2C();

    //Initialize UART for bt
    init_UART();

    mma_write(ctrl_reg1 ,0x00);
    //delay(10);

    mma_write(xyz_data_cfg , 0x00); // 2G full range mode
    //delay(1);

    mma_write(ctrl_reg1, 0x01); // Output data rate at 800Hz, no auto wake, no auto scale adjust, no fast read mode
    //delay(1);

    uint8_t xMSB, xLSB, yMSB, yLSB, zMSB, zLSB;

    while(1){
        xMSB = mma_read(0x01);
        xLSB = mma_read(0x02);
        yMSB = mma_read(0x03);
        yLSB = mma_read(0x04);
        zMSB = mma_read(0x05);
        zLSB = mma_read(0x06);

    }
//    while (1)
//    {
//        //while (UCB0CTLW0 & UCTXSTP);             // Ensure stop condition got sent
//        UCB0CTLW0 |= UCTXSTT;                    // I2C start condition
//        while (UCB0CTLW0 & UCTXSTT);             // Start condition sent?
//        UCB0CTLW0 |= UCTXSTP;                    // I2C stop condition
//        __bis_SR_register(LPM3);        // Enter LPM0 w/ interrupts
//
//    }

}

void init_I2C(){

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
    UCB0CTLW0 |= UCMST | UCMODE_3 | UCSYNC | UCSSEL_2 | UCSWRST;// | UCTXACK;

    //Setup CTLW1
    //Might not need to do this

    UCB0BRW = 10; //SMCLK/10 = ~100KHz???

    //Slave Address - Do I need to declare this
    //Figure out the slave address
    UCB0I2CSA = 0x1D;

    //Disable UCSWRST
    UCB0CTLW0 &= ~UCSWRST;

    //Enable interrupt
    UCB0IE |= UCRXIE1;
}

void init_UART()
{
    // Configure GPIO
    P2SEL1 |= BIT5 | BIT6;                    // USCI_A0 UART operation
    P2SEL0 &= ~(BIT5 | BIT6);

    // Configure USCI_A0 for UART mode
    UCA1CTLW0 = UCSWRST;                      // Put eUSCI in reset
    UCA1CTLW0 |= UCSSEL__SMCLK;               // CLK = SMCLK
    // Baud Rate calculation
    // 1000000/(16*9600) = 6
    // User's Guide Table 21-4: UCBRSx = 0x04
    // UCBRFx = int ( (52.083-52)*16) = 1
    UCA1BR0 = 6;                             // 8000000/16/9600
    UCA1BR1 = 0x00;
    UCA1MCTLW |= UCOS16 | UCBRF_8;
    UCA1CTLW0 &= ~UCSWRST;                    // Initialize eUSCI
    UCA1IE |= UCRXIE;                         // Enable USCI_A0 RX interrupt
}


void mma_write(uint8_t register_address, uint8_t data)
{
    UCB0CTLW0 |= UCTXSTT;         // I2C TX, start condition
    while (UCB0CTLW0 & UCTXSTT);               // Start condition sent?

    //Going to write to the accel
    UCB0TXBUF = 0x3A;
    while ((IFG2 & UCB0TXIFG) == 0);

    //This is the reg im going to write to
    UCB0TXBUF = register_address;
    while ((IFG2 & UCB0TXIFG) == 0);

    //This is what im going to write to that address
    UCB0TXBUF = data;
    while ((IFG2 & UCB0TXIFG) == 0);

    //Stop condition
    UCB0CTLW0 |= UCTXSTP;            // No Repeated Start: stop condition
    while (UCB0CTL1 & UCTXSTP);         // Check stop sent
}



uint8_t mma_read(uint8_t register_address)
{
    uint8_t result = 0xFF;

    UCB0CTLW0 |= UCTR + UCTXSTT;         // I2C TX, start condition
    UCB0TXBUF = register_address;
    while (UCB0CTLW0 & UCTXSTT);               // Start condition sent?
    while ((IFG2 & UCB0TXIFG) == 0);

    UCB0CTLW0 &= ~UCTR;  //Sets the master as a receiver
    UCB0CTLW0 |= UCTXSTT;    //Start Condition sends address of slave
    while (UCB0CTLW0 & UCTXSTT);         // Check start sent

    UCB0CTLW0 |= UCTXSTP;            // No Repeated Start: stop condition
    while ((IFG2 & UCB0RXIFG) == 0);
    result = UCB0RXBUF;
    IFG2 &= ~UCB0RXIFG;

    while (UCB0CTLW0 & UCTXSTP);         // Check stop sent

    return result;
}



#pragma vector = USCI_I2C_UCRXIFG0
__interrupt void Accelerometer_Interrupt(void){
    printf("check");
    fflush(stdout);
}











