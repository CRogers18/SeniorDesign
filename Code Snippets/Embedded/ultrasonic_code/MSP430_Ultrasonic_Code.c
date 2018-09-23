#include "msp430.h"
#include <stdint.h>

float sensorData[6] = {0};

int main(void)
{
  WDTCTL = WDTPW | WDTHOLD;                 // Stop Watchdog

  // Configure GPIO for the Bluetooth module
  P2SEL1 |= BIT5 | BIT6;                    // USCI_A0 UART operation
  P2SEL0 &= ~(BIT5 | BIT6);

  //COnfigure GPIO for the ultrasonic

  // Disable the GPIO power-on default high-impedance mode to activate
  // previously configured port settings
  PM5CTL0 &= ~LOCKLPM5;


  init_UART();

  __bis_SR_register(LPM3_bits | GIE);       // Enter LPM3, interrupts enabled
  __no_operation();                         // For debugger
}

void ultrasonic_ping() {

  //Ping the echo
  P1DIR |= 0x01;  // set connected pin to output
  P1OUT ^= 0x01;  // drive connected pin high
  
  //delay 10us or 10 cycles at 1 MHz
  __delay_cycles(10); // bad way to do delay, should use timer w/ interrupts for low-power
  P1OUT ^= 0x01;  // drive connected pin low

  //ping the trigger
  P1DIR |= 0x01;  // set connected pin to input

  // wait 50ms with timer interrupts here

  // timer PWM configuration needed to measure pulse duration
  float pulseDuration;

  // hand-off data to array, division op will be done on phone app, send raw data over UART
  sensorData[0] = pulseDuration;

  floatToBuffer(sensorData[0]);
  //distance = duration / 296; also try to divide bt 58 to get distance in centimeters
}

void OUTA_UART(unsigned char data){

    // wait for the transmit buffer to be empty before sending data
    do{}
    while((UCA1IFG & 0x02) == 0);

    // send the data to the transmit buffer
    UCA1TXBUF = data;
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

    OUTA_UART('\n');
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
