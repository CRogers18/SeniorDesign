/* --COPYRIGHT--,BSD
 * Copyright (c) 2017, Texas Instruments Incorporated
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * *  Neither the name of Texas Instruments Incorporated nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * --/COPYRIGHT--*/
#include "driverlib.h"
#include <stdio.h>
#include <stdint.h>
#include "msp430fr5969.h"

void masterWriteSomething(uint16_t baseAddress, uint8_t txData);
uint8_t masterReadSomething (uint16_t baseAddress);
void init_UART();
void OUTA_UART(unsigned char data);

//*****************************************************************************
//! This example shows how to configure the I2C module as a master for
//! single byte reception in interrupt driven mode. The address of the slave
//! module that the master is communicating with also set in this example.
//!
//!  Description: This demo connects two MSP430's via the I2C bus. The master
//!  reads from the slave. This is the MASTER CODE. The data from the slave
//!  transmitter begins at 0 and increments with each transfer.
//!  The USCI_B0 RX interrupt is used to know when new data has been received.
//!  ACLK = n/a, MCLK = SMCLK = BRCLK =  DCO = 1MHz
//!
//!                                /|\  /|\
//!               MSP430FR5969      10k  10k     MSP430FR5969
//!                   slave         |    |        master
//!             -----------------   |    |   -----------------
//!           -|XIN  P1.6/UCB0SDA|<-|----+->|P1.6/UCB0SDA  XIN|-
//!            |                 |  |       |                 | 32kHz
//!           -|XOUT             |  |       |             XOUT|-
//!            |     P1.7/UCB0SCL|<-+------>|P1.7/UCB0SCL     |
//!            |                 |          |             P1.0|--> LED
//!

//! This example uses the following peripherals and I/O signals.  You must
//! review these and change as needed for your own board:
//! - I2C peripheral
//! - GPIO Port peripheral (for I2C pins)
//! - SCL2
//! - SDA
//!
//! This example uses the following interrupt handlers.  To use this example
//! in your own application you must add these interrupt handlers to your
//! vector table.
//! - USCI_B0_VECTOR.
//!
//
//*****************************************************************************
//*****************************************************************************
//
//Set the address for slave module. This is a 7-bit address sent in the
//following format:
//[A6:A5:A4:A3:A2:A1:A0:RS]
//
//A zero in the "RS" position of the first byte means that the master
//transmits (sends) data to the selected slave, and a one in this position
//means that the master receives data from the slave.
//
//*****************************************************************************
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



#define SLAVE_ADDRESS 0x1D

uint8_t RXData;
void main (void)
{
    //Stop WDT
    WDT_A_hold(WDT_A_BASE);



    //Set DCO frequency to 1MHz
    CS_setDCOFreq(CS_DCORSEL_0,CS_DCOFSEL_0);
    //Set ACLK = VLO with frequency divider of 1
    CS_initClockSignal(CS_ACLK,CS_VLOCLK_SELECT,CS_CLOCK_DIVIDER_8);
    //Set SMCLK = DCO with frequency divider of 1
    CS_initClockSignal(CS_SMCLK,CS_DCOCLK_SELECT,CS_CLOCK_DIVIDER_8);
    //Set MCLK = DCO with frequency divider of 1
    CS_initClockSignal(CS_MCLK,CS_DCOCLK_SELECT,CS_CLOCK_DIVIDER_8);



    // Configure Pins for I2C
    //Set P1.6 and P1.7 as Secondary Module Function Input.
    /*

    * Select Port 1
    * Set Pin 6, 7 to input Secondary Module Function, (UCB0SIMO/UCB0SDA, UCB0SOMI/UCB0SCL).
    */
    GPIO_setAsPeripheralModuleFunctionInputPin(
        GPIO_PORT_P1,
        GPIO_PIN6 + GPIO_PIN7,
        GPIO_SECONDARY_MODULE_FUNCTION
    );

    //Set P1.0 as an output pin.
    /*

     * Select Port 1
     * Set Pin 0 as output
     */
    GPIO_setAsOutputPin(
        GPIO_PORT_P1,
        GPIO_PIN0
    );

    /*
     * Disable the GPIO power-on default high-impedance mode to activate
     * previously configured port settings
     */
    PMM_unlockLPM5();

    EUSCI_B_I2C_initMasterParam param = {0};
    param.selectClockSource = EUSCI_B_I2C_CLOCKSOURCE_SMCLK;
    param.i2cClk = CS_getSMCLK();
    param.dataRate = EUSCI_B_I2C_SET_DATA_RATE_400KBPS;
    param.byteCounterThreshold = 1;
    param.autoSTOPGeneration = EUSCI_B_I2C_SEND_STOP_AUTOMATICALLY_ON_BYTECOUNT_THRESHOLD;
    EUSCI_B_I2C_initMaster(EUSCI_B0_BASE, &param);

    //Specify slave address
//    EUSCI_B_I2C_setSlaveAddress(EUSCI_B0_BASE,
//        SLAVE_ADDRESS
//        );
    UCB0I2CSA = 0x1D;

    //Set Master in receive mode
//    EUSCI_B_I2C_setMode(EUSCI_B0_BASE,
//        EUSCI_B_I2C_RECEIVE_MODE
//        );
    UCB0CTLW0 &= ~UCTR;

    //Enable I2C Module to start operations
//    EUSCI_B_I2C_enable(EUSCI_B0_BASE);
    UCB0CTLW0 &= ~UCSWRST;

    EUSCI_B_I2C_clearInterrupt(EUSCI_B0_BASE,
        EUSCI_B_I2C_RECEIVE_INTERRUPT0 +
        EUSCI_B_I2C_BYTE_COUNTER_INTERRUPT
        );

    //Enable master Receive interrupt
    EUSCI_B_I2C_enableInterrupt(EUSCI_B0_BASE,
        EUSCI_B_I2C_RECEIVE_INTERRUPT0 +
        EUSCI_B_I2C_BYTE_COUNTER_INTERRUPT
        );

    //Set up the accelerometer
    masterWriteSomething(CTRL_REG2, 0x40); //Reset
    masterWriteSomething(CTRL_REG1, 0x28); //Set the device in 12.5 Hz ODR, Standby
    masterWriteSomething(FF_MT_CFG, 0xD8); //Set Motion Detection by setting the “OR” condition OAE = 1, enabling X, Y and the latch
    masterWriteSomething(FF_MT_THS, 0x01);  // Threshold Setting Value
    masterWriteSomething(FF_MT_COUNT, 0x00);// Set the debounce counter to eliminate false readings for 12.5 Hz sample rate with a requirement of 80 ms timer.
    masterWriteSomething(CTRL_REG4, 0x04); // Enable Motion/Freefall Interrupt Function in the System (CTRL_REG4)
    masterWriteSomething(CTRL_REG5, 0x04); // Route the Motion/Freefall Interrupt Function to INT1 hardware pin (CTRL_REG5)

    //masterWriteSomething(CTRL_REG2, masterReadSomething(CTRL_REG2)|0x18); // Low Power mode
    //masterWriteSomething(CTRL_REG1, masterReadSomething(CTRL_REG1)|0x01); // Put the device in Active Mode
    //Took this and made it its own function

//    __bis_SR_register(LPM3_bits | GIE);       // Enter LPM3, interrupts enabled
//      __no_operation();                         // For debugger
    //while(1);
}

void ping_accel()
{
    uint8_t xMSB;
    //uint8_t xLSB;
    uint8_t yMSB;
    //uint8_t yLSB;
    uint8_t zMSB;
    //uint8_t zLSB;
    uint16_t x_value;
    uint16_t y_value;
    uint16_t z_value;

     while (1)
    {
      __delay_cycles(2000);

     // I2C start condition
      //MSB is 8 bits and LSB is right most 4 bits
      xMSB = masterReadSomething(0x01);
      //xLSB = masterReadSomething(0x02);
      yMSB = masterReadSomething(0x03);
      //yLSB = masterReadSomething(0x04);
      zMSB = masterReadSomething(0x05);
      //zLSB = masterReadSomething(0x06);
      OUTA_UART(xMSB);
//      OUTA_UART(yMSB);
//      OUTA_UART(zMSB);


//      printf("%c ", RXData);
//      fflush(stdout);

        }
}

void masterWriteSomething(uint16_t baseAddress, uint8_t txData)
{
    //Set USCI in Transmit mode
    UCB0CTLW0 |= UCTR;

    //Start Condition
    UCB0CTLW0 |= UCTXSTT;

    //Check for start condition
    while(UCB0CTLW0 & UCTXSTT);

    //Send that I want to write something
    UCB0TXBUF = 0x3A;

    //Send Reg to write to
    UCB0TXBUF = baseAddress;

    //Send data to write
    UCB0TXBUF = txData;

    //Send stop condition
    UCB0CTLW0 |= UCTXSTP;

    //Clear transmit interrupt flag before enabling interrupt again
    UCB0IFG &= ~(UCTXIFG);

    //Reinstate transmit interrupt enable
    UCB0IE |= UCTXIE;
}

uint8_t masterReadSomething (uint16_t baseAddress)
{
    //Set USCI in Receive mode
    UCB0CTLW0 &= ~UCTR;

    //Send start condition
    UCB0CTLW0 |= UCTXSTT;

    //Poll for start condition transmission
    while(UCB0CTLW0 & UCTXSTT);

    //Im going to read a register
    UCB0TXBUF = 0x3A;

    //Going to read this reg data
    UCB0TXBUF = baseAddress;

    //Send REPEATED start condition
    UCB0CTLW0 |= UCTXSTT;

    //Im going to read a register
    UCB0TXBUF = 0x3B;

    //Poll for receive interrupt flag.
    while (!(UCB0IFG & UCRXIFG));

    //Send stop condition
    UCB0CTLW0 |= UCTXSTP;

    //Send single byte data.
    return UCB0RXBUF;
}

void OUTA_UART(unsigned char data){


    // wait for the transmit buffer to be empty before sending data
    do{}
    while((UCA1IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA1TXBUF = data;
}

void init_UART()
{

    // Configure GPIO
    P2SEL1 |= BIT5 | BIT6;                    // USCI_A0 UART operation
    P2SEL0 &= ~(BIT5 | BIT6);

    // Disable the GPIO power-on default high-impedance mode to activate
    // previously configured port settings
    PM5CTL0 &= ~LOCKLPM5;

    // Configure USCI_A0 for UART mode
    UCA1CTLW0 = UCSWRST;                      // Put eUSCI in reset
    UCA1CTLW0 |= UCSSEL__ACLK;               // CLK = ACLK
    // Baud Rate calculation
    // 8000000/(16*9600) = 52.083
    // Fractional portion = 0.083
    // User's Guide Table 21-4: UCBRSx = 0x04
    // UCBRFx = int ( (52.083-52)*16) = 1
    UCA1BR0 = 52;                             // 8000000/16/9600
    UCA1BR1 = 0x00;
    UCA1MCTLW |= UCOS16 | UCBRF_1;
    UCA1CTLW0 &= ~UCSWRST;                    // Initialize eUSCI
    UCA1IE |= UCRXIE;                         // Enable USCI_A0 RX interrupt
}

#if defined(__TI_COMPILER_VERSION__) || defined(__IAR_SYSTEMS_ICC__)
#pragma vector=USCI_A1_VECTOR
__interrupt void USCI_A1_ISR(void)
#elif defined(__GNUC__)
void __attribute__ ((interrupt(USCI_A1_VECTOR))) USCI_A1_ISR (void)
#else
#error Compiler not supported!
#endif
{
    printf("WE GOT SOMETHING\n");
            fflush(stdout);
  switch(__even_in_range(UCA1IV, USCI_UART_UCTXCPTIFG))
  {
    case USCI_NONE: break;
    case USCI_UART_UCRXIFG:
//      while(!(UCA1IFG&UCTXIFG));
//      UCA1TXBUF = UCA1RXBUF;

        ping_accel();
        //OUTA_UART(UCA1RXBUF);
      __no_operation();
      break;
    case USCI_UART_UCTXIFG: break;
    case USCI_UART_UCSTTIFG: break;
    case USCI_UART_UCTXCPTIFG: break;
  }
}
