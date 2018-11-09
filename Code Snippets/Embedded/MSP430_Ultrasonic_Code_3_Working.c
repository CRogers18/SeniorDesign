#include <msp430.h>
#include <stdint.h>
#include <stdio.h>

uint16_t miliseconds_1, miliseconds_2, miliseconds_3;
int distance_1, distance_2, distance_3;
uint16_t sensor_1, sensor_2, sensor_3;
uint8_t sensor_1_MSB, sensor_1_LSB, sensor_2_MSB, sensor_2_LSB, sensor_3_MSB, sensor_3_LSB;

//Ultrasonic 1,2,3 Trig and Echo are as follows
//P4.2, P4.3    |   P2.4, P2.2    |    P1.4, P1.3
#define trigPin_1 BIT2
#define echoPin_1 BIT3
#define trigPin_2 BIT4
#define echoPin_2 BIT2
#define trigPin_3 BIT4
#define echoPin_3 BIT3

__interrupt void Timer0_A0(void);
__interrupt void Port1_Vector(void);
void ultrasonic_ping();
void init_UART();

int main(void)
{
    WDTCTL = WDTPW | WDTHOLD;                 // Stop Watchdog
    PM5CTL0 &= ~LOCKLPM5;                   // Disable the GPIO power-on default high-impedance mode
                                                // to activate previously configured port settings

    // Startup clock system with DCO 1MHz
    CSCTL0_H = CSKEY >> 8;                    // Unlock clock registers
    CSCTL1 = DCOFSEL_0 | DCORSEL;             // Set DCO to 1MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1;     // Set all dividers
    CSCTL0_H = 0;    // Lock CS registers

    init_UART();

    //CCR0 Interrupt Enable
    //Set up the Timer_A0 capture compare register
    TA0CCTL0 = CCIE;

    TA0CCR0 = 1000;

    //Set up the timer A0 control
    TA0CTL = TASSEL_2 | MC_1;

    //Clear the interrupt flag, for Pins
    P1IFG = 0x00;
    P2IFG = 0x00;
    P3IFG = 0x00;
    P4IFG = 0x00;

    P1DIR |= 0x01;                            // P1.0 as output for LED
    P1OUT |= 0x01;                           // turn LED on


    __bis_SR_register(GIE);       // Enter LPM3, interrupts enabled
    __no_operation();                         // For debugger

    ultrasonic_ping();
}

void ultrasonic_ping(){

    P4DIR |= trigPin_1;          // trigger pin as output
    P4DIR &= ~echoPin_1;         // make pin P1.2 input (ECHO)

    P2DIR |= trigPin_2;
    P2DIR &= ~echoPin_2;

    P1DIR |= trigPin_3;
    P1DIR &= ~echoPin_3;

    while(1){

        //Ultrasonic Sensor 1
        P4IE &= ~BIT0;          // disable interupt

        P4OUT &= ~trigPin_1; //Stop pulse
        P4OUT |= trigPin_1;          // generate pulse
        __delay_cycles(10);             // for 10us
        P4OUT &= ~trigPin_1;                 // stop pulse

        P4IFG = 0x00;                   // clear flag just in case anything happened before
        P4IE |= echoPin_1;           // enable interrupt on ECHO pin
        P4IES &= ~echoPin_1;         // rising edge on ECHO pin
        __delay_cycles(40000);          // delay for 30ms (after this time echo times out if there is no object detected)

        //Ultrasonic Sensor 2
        P2IE &= ~BIT0;          // disable interupt

        P2OUT &= ~trigPin_2; //Stop pulse
        P2OUT |= trigPin_2;          // generate pulse
        __delay_cycles(10);             // for 10us
        P2OUT &= ~trigPin_2;                 // stop pulse

        P2IFG = 0x00;                   // clear flag just in case anything happened before
        P2IE |= echoPin_2;           // enable interrupt on ECHO pin
        P2IES &= ~echoPin_2;         // rising edge on ECHO pin
        __delay_cycles(40000);          // delay for 30ms (after this time echo times out if there is no object detected)

        //Ultrasonic Sensor 3
        P1IE &= ~BIT0;          // disable interupt

        P1OUT &= ~trigPin_3; //Stop pulse
        P1OUT |= trigPin_3;          // generate pulse
        __delay_cycles(10);             // for 10us
        P1OUT &= ~trigPin_3;                 // stop pulse

        P1IFG = 0x00;                   // clear flag just in case anything happened before
        P1IE |= echoPin_3;           // enable interrupt on ECHO pin
        P1IES &= ~echoPin_3;         // rising edge on ECHO pin
        __delay_cycles(40000);          // delay for 30ms (after this time echo times out if there is no object detected)


        //Sensor is the duration to send to the phone
        sensor_1_MSB = (sensor_1 & 0xFF00) >> 8;
        sensor_1_LSB = (sensor_1 & 0x00FF);

        sensor_2_MSB = (sensor_2 & 0xFF00) >> 8;
        sensor_2_LSB = (sensor_2 & 0x00FF);

        sensor_3_MSB = (sensor_3 & 0xFF00) >> 8;
        sensor_3_LSB = (sensor_3 & 0x00FF);
        OUTA_UART(0x1F);

        OUTA_UART(sensor_1_MSB);
        OUTA_UART(sensor_1_LSB);

        OUTA_UART(sensor_2_MSB);
        OUTA_UART(sensor_2_LSB);

        OUTA_UART(sensor_3_MSB);
        OUTA_UART(sensor_3_LSB);





        //distance_1 = sensor_1/58;           // converting ECHO length into cm
//        if(distance < 20 && distance != 0) P1OUT |= 0x01;  //turning LED on if distance is less than 20cm and if distance isn't 0.
//        else P1OUT &= ~BIT0;

        }
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


void OUTA_UART(uint8_t data){

    // wait for the transmit buffer to be empty before sending data
    do{}
    while((UCA1IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA1TXBUF = data;
}



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

    //OUTA_UART('\n');
}

#pragma vector=TIMER0_A0_VECTOR
__interrupt void Timer0_A0(void)
{
    miliseconds_1++;
    miliseconds_2++;
    miliseconds_3++;
}

#pragma vector=PORT4_VECTOR
__interrupt void Port4_Vector(void)
{

    if(P4IFG & echoPin_1)  //is there interrupt pending?
        {
          if(!(P4IES & echoPin_1)) // is this the rising edge?
          {
            TA0CTL |= TACLR;   // clears timer A
            //TA0CCR0 = 0x01; //Start the timer
            miliseconds_1 = 0;
            P4IES |= echoPin_1;  //falling edge
          }
          else
          {
              //TA0CCR0 = 0x00; //Stop the timer
            sensor_1 = (long)miliseconds_1 + (long)TA0R;    //calculating ECHO length
              //sensor_1 = TA0R;

          }
    P4IFG &= ~echoPin_1;             //clear flag

    }
}

#pragma vector=PORT2_VECTOR
__interrupt void Port3_Vector(void)
{

    if(P2IFG & echoPin_2)  //is there interrupt pending?
        {
          if(!(P2IES & echoPin_2)) // is this the rising edge?
          {
            TA0CTL |= TACLR;   // clears timer A
            miliseconds_2 = 0;
            P2IES |= echoPin_2;  //falling edge
          }
          else
          {
            sensor_2 = (long)miliseconds_2*1000 + (long)TA0R;    //calculating ECHO lenght

          }
    P2IFG &= ~echoPin_2;             //clear flag

    }
}




#pragma vector=PORT1_VECTOR
__interrupt void Port1_Vector(void)
{

    if(P1IFG & echoPin_3)  //is there interrupt pending?
        {
          if(!(P1IES & echoPin_3)) // is this the rising edge?
          {
            TA0CTL|=TACLR;   // clears timer A
            miliseconds_3 = 0;
            P1IES |= echoPin_3;  //falling edge
          }
          else
          {
            sensor_3 = (long)miliseconds_3*1000 + (long)TA0R;    //calculating ECHO lenght

          }
    P1IFG &= ~echoPin_3;             //clear flag

    }
}













//#if defined(__TI_COMPILER_VERSION__) || defined(__IAR_SYSTEMS_ICC__)
//#pragma vector=USCI_A1_VECTOR
//__interrupt void USCI_A1_ISR(void)
//#elif defined(__GNUC__)
//void __attribute__ ((interrupt(USCI_A1_VECTOR))) USCI_A1_ISR (void)
//#else
//#error Compiler not supported!
//#endif
//{
//  switch(__even_in_range(UCA1IV, USCI_UART_UCTXCPTIFG))
//  {
//    case USCI_NONE: break;
//    case USCI_UART_UCRXIFG:
////      while(!(UCA1IFG&UCTXIFG));
////      UCA1TXBUF = UCA1RXBUF;
//        floatToBuffer(12.3456789);
//        ultrasonic_ping();
//        UCA1IFG = 0x00;
//        //OUTA_UART(UCA1RXBUF);
//      __no_operation();
//      break;
//    case USCI_UART_UCTXIFG: break;
//    case USCI_UART_UCSTTIFG: break;
//    case USCI_UART_UCTXCPTIFG: break;
//  }
//}


