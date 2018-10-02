#include "msp430fr5969.h"
#include <stdint.h>
#include <stdio.h>

float sensorData[6] = {0};
unsigned int up_counter;
unsigned int distance_cm;

__interrupt void Timer0_A0(void);
void ultrasonic_ping();
void init_UART();

int main(void)
{
    WDTCTL = WDTPW | WDTHOLD; //Stop Watchdog

    // Disable the GPIO power-on default high-impedance mode to activate
    // previously configured port settings
    PM5CTL0 &= ~LOCKLPM5;

    // Configure GPIO for the Bluetooth module
    P2SEL1 |= (BIT5 | BIT6);
    P2SEL0 &= ~(BIT5 | BIT6);

    //Configure GPIO for the ultrasonic
    //Trigger is an output
    P1DIR |= BIT5;
    P1OUT &= ~BIT5;     //Set to low to wait for initiation

    //Echo is an input
    P1DIR &= ~(BIT6);
    P1SEL0 |= BIT1;   //Sets this for the Timer A0 to work on this input
    P1SEL1 |= BIT1;

    //init_UART();

    //Configure the clock speed here - set to 8MHz
    CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
    CSCTL1 = DCOFSEL_0 | DCORSEL;             // Set DCO to 8MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
    CSCTL0_H = 0;                             // Lock CS registers



    //Set up the Timer_A0 capture compare register
    TA0CCTL0 = CM_3 | SCS | CCIE | CAP | CCIS_3;

    //Set up the timer A0 control
    TA0CTL = TASSEL_0 | MC_2 | ID_0 | TAIE;

    __bis_SR_register(GIE); // Enter LPM3, interrupts enabled
    //__no_operation();                   // For debugger

    for(;;){
        ultrasonic_ping();

        printf("The distance is: %x\n", distance_cm);
        fflush(stdout);
    }


}

//Trigger is set to high, wait 10us, set low
//Read from echo to get the time
void ultrasonic_ping() {

    //Ping the trigger
    P1OUT ^= BIT5;  // drive connected pin high

    //delay 10us or 10 cycles at 1 MHz
    __delay_cycles(10); // bad way to do delay, should use timer w/ interrupts for low-power
    P1OUT ^= BIT5;  // drive connected pin low

    //while loop to wait for Echo to go high
    while(P1IN & BIT6 == 0x00){
    }

    //get the current time
    unsigned int time1 = TA0R;

    //while loop to wait for the echo to go low
    while(P1IN & BIT6 == BIT6){
    }

    //get the current time
    unsigned int time2 = TA0R;
    printf("The timer says that it is %i\n", TA0R);

    //the difference is what we need
    unsigned int total_time = time2 - time1;

    __delay_cycles(60000);

    // wait 50ms with timer interrupts here
    //Need to set up Timer Control, Timer Compare Control, and the hardcoded time for the delay

//    // timer PWM configuration needed to measure pulse duration
//    float pulseDuration;
//
//    // hand-off data to array, division op will be done on phone app, send raw data over UART
//    sensorData[0] = pulseDuration;
//
//    floatToBuffer(sensorData[0]);

    //distance = duration / 296; also try to divide bt 58 to get distance in centimeters
    //Alternate equation for cm
    //distance = ( (duration * 340) / 100 ) / 2
}



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


void init_UART(){
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


//Output 8 bit char to UART
void OUTA_UART(unsigned char data){

    // wait for the transmit buffer to be empty before sending data
    do{}
    while((UCA1IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA1TXBUF = data;
}
