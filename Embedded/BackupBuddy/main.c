#include "msp430fr5969.h"
#include <stdint.h>
#include <stdio.h>

// For reference later when ISR has run
typedef struct timestampData
{
    uint32_t tarValueStart;
    uint32_t tarValueEnd;
    uint32_t loopIteration;
} tsData;

tsData timeInfo[3] = {0};

//Holds Accelerometer and Ultrasonic information
uint32_t sensorData[6] = {0};

// Gives whether the pin has already begun to echo
uint8_t echoStatus[3] = {0};

// Counter variables, both limited to 255
uint8_t i, j, loopCount, loop = 1;

void ultrasonic_ping();
void init_UART();

int main(void)
{
    WDTCTL = WDTPW | WDTHOLD; //Stop Watchdog

    // Disable the GPIO power-on default high-impedance mode to activate
    // previously configured port settings
//    PM5CTL0 &= ~LOCKLPM5;

    // Configure GPIO for the Bluetooth module
    P2SEL1 |= (BIT5 | BIT6);
    P2SEL0 &= ~(BIT5 | BIT6);

    //Configure GPIO for the ultrasonic

    // 1 is output, 0 is input

    //Trigger is an output
    P1DIR &= ~BIT5;  // Clears pin 5 and ensures its 0
    P1DIR |= BIT5;   // Sets pin 5 of P1 to output
    P1IN &= ~BIT5;

//    P1DIR &= ~BIT6;  // Clears pin 6 to ensure its 0
//    P1IN &= ~BIT6;   // Ensures pin 6 is set to 0 for echo, a 1 would indicate data

    // P1SEL0 = ~BIT0;
    // P1SEL1 = ~BIT0;

//    P1SEL0 = BIT1;     //Sets this for the timer to work with it
//    P1SEL1 = BIT1;

    // Configure echo pin interrupt
    P1IES &= ~BIT5;
    P1IE |= BIT5;
    P1IFG &= ~BIT5;

    //init_UART();

    //Set up the Timer_A0 interrupts
    //Didnt set up capture input
//    TA0CCTL0 = CM_3 | SCS | CCIE | CAP | CCIS_0;
    //CCTL0 |= CM_3 + SCS + CCIS_0 + CAP + CCIE;

    TA0CTL = TASSEL_2 | ID_0 | MC_2 | TACLR | TAIE;
    TA0CTL &= ~TAIFG;

    __bis_SR_register(GIE); // Enter LPM3, interrupts enabled
    //__no_operation();                   // For debugger

    for(i = 0; i < 500; i++)
        ultrasonic_ping(BIT5);
}

//Trigger is set to high, wait 10us, set low
//Read from echo to get the time
void ultrasonic_ping(uint16_t pin)
{
    // 5 is both trigger and echo

    // Disable interrupts on pin 5 temporarily
    P1IE &= ~BIT5;

    // pin 5 goes high to trigger
    P1OUT ^= BIT5;
    __delay_cycles(20);

    // pin 5 back to low to release trigger
    P1OUT ^= BIT5;

    // configures pin 5 to input since 1 pin is being used in the schematic
    P1DIR &= ~BIT5;

    // re-enable interrupts for pin 5
    P1IE |= BIT5;

    loopCount = 0;

    // 8.192 ms * 6 gets us pretty close to 50 ms duration (approx. 49.15 ms)
    for(j = 0; j < 6; j++)
    {
        // On timer roll back, increment counter
        while((TA0CTL & TAIFG) == 0) {  }
    }

    // Lets just print one value out rn bois
    for(j = 0; j < 1; j++)
    {
        timeInfo[j].tarValueEnd = (timeInfo[j].loopIteration * 65535)  + (uint32_t) TA0R;
        uint32_t duration = timeInfo[j].tarValueEnd - timeInfo[j].tarValueStart;
        printf("Pulse duration was: %u\n", duration);
        fflush(stdout);
        echoStatus[i] = 0;
    }
}

//Output 8 bit char to UART
void OUTA_UART(unsigned char data)
{
    // wait for the transmit buffer to be empty before sending data
    do{}
    while((UCA1IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA1TXBUF = data;
}

void init_UART()
{
    // Startup clock system with max DCO setting ~8MHz
    CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
    CSCTL1 = DCOFSEL_3 | DCORSEL;             // Set DCO to 8MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
    CSCTL0_H = 0;                             // Lock CS registers

    // Configure USCI_A0 for UART mode
    UCA1CTLW0 = UCSWRST;                      // Put eUSCI in reset
    UCA1CTLW0 |= UCSSEL__SMCLK;               // CLK = SMCLK
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

//Converts float to 4 different 8 bit data points to be sent
//over UART
void floatToBuffer(float data)
{
    //Gets the binary representation of a float
    union
    {
        float    floatData;
        uint32_t bitData;
    } pun = { .floatData = data };

    //Mask allows us to gather 8 bits at a time
    uint32_t mask = 0xFF000000;
    uint32_t val;
    int i;
    uint8_t c;

    //Evalutes from MSB to LSB
    for(i = 3; i > -1; i--)
    {
        // Get 8 bits of MSB
        uint32_t val = (pun.bitData & mask);

        val >>= (i*8);

        // printBits(sizeof(val), &val);
        mask >>= 8;
        c = (uint8_t) val;
        OUTA_UART(c);
    }

    //Is this necessary?
    OUTA_UART('\n');
}

//USCI_A1 Interrupt Vector - Operates on a transmission recieve from UART
#if defined(__TI_COMPILER_VERSION__) || defined(__IAR_SYSTEMS_ICC__)
#pragma vector=USCI_A1_VECTOR
__interrupt void USCI_A1_ISR(void)
#elif defined(__GNUC__)
void __attribute__ ((interrupt(USCI_A1_VECTOR))) USCI_A1_ISR (void)
#else
#error Compiler not supported!
#endif
{
    switch(__even_in_range(UCA1IV, USCI_UART_UCTXCPTIFG))
    {
        case USCI_NONE: break;
        case USCI_UART_UCRXIFG:
          //Code goes here
          __no_operation();
          break;
        case USCI_UART_UCTXIFG: break;
        case USCI_UART_UCSTTIFG: break;
        case USCI_UART_UCTXCPTIFG: break;
    }
}

/*
#pragma vector=TIMER0_A0_VECTOR
__interrupt void Timer0_A0(void)
{
    printf("We out here\n");
    fflush(stdout);

    if (TA0CCTL0 & CCI)            // Raising edge
    {
        up_counter = TA0CCR0;      // Copy counter to variable
    }

    else                        // Falling edge
    {
        // Formula: Distance in cm = (Time in uSec)/58
        distance_cm = (TA0CCR0 - up_counter)/58;
    }

    printf("The up counter is: %x\n", up_counter);
        fflush(stdout);
    TA0CTL &= ~TAIFG;           // Clear interrupt flag - handled
} */

#pragma vector = TIMER0_A1_VECTOR; // 0 used because LaunchPad has 2 timers
__interrupt void TA0_ISR(void)
{
    // check for rollback event
    if ( (TA0CTL & TAIFG) != 0)
    {
        loopCount++;
        TA0CTL &= ~TAIFG;
    }
}

#pragma vector=PORT1_VECTOR
__interrupt void EchoPinHigh()
{
    if(!echoStatus[0] && (P1IN & BIT5 == BIT5))
    {
        // beginTS[0] = (loopCount * 65535) + (uint32_t) TAR;
        timeInfo[0].tarValueStart = (uint32_t) TA0R;
        timeInfo[0].loopIteration = (uint32_t) loopCount;
        echoStatus[0] = 1;
    }

    // Reset interrupt flag on the pin
    P1IFG &= ~BIT5;
}
