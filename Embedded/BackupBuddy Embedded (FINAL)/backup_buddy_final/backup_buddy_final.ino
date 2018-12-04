//Test code for the Ultrasonic Sensors using
//the MSP430

#include <Wire.h>
#include <stdint.h>

#define trigPin_1 P4_3
#define echoPin_1 P4_2
#define trigPin_2 P1_5
#define echoPin_2 P1_4
#define trigPin_3 P1_3
#define echoPin_3 P1_2

// Defined with temporary pin until final pin is known
#define rpiSecurity P3_4
#define rpiWake P4_6    // TODO: Switch back to 4_6 for PCB
#define rpiStop P4_5

// MMA8452Q Address, single slave on the bus, 1C for PCB, 1D for dev board
int adress_acc=0x1C;

// MMA8452Q Register Addresses
uint8_t ctrl_reg1 = 0x2A;
uint8_t ctrl_reg2 = 0x2B;
uint8_t ctrl_reg3 = 0x2C;
uint8_t ctrl_reg4 = 0x2D;
uint8_t ctrl_reg5 = 0x2E;
uint8_t int_source = 0x0C;
uint8_t status_ = 0x00;
uint8_t f_setup = 0x09;
uint8_t out_x_msb = 0x01;
uint8_t out_y_msb = 0x03;
uint8_t out_z_msb = 0x05;
uint8_t sysmod = 0x0B;
uint8_t xyz_data_cfg = 0x0E;

// Counter variable
uint8_t i = 0;

// CFG: ELE (7 = 0), OAE (6 = 1), ZEFE (5 = 1), YEFE (4 = 1), XEFE (3 = 1), (2-0 = 0)
uint8_t motion_src_cfg = 0x15;

uint16_t duration_1, duration_2, duration_3;
uint8_t duration_1_MSB, duration_1_LSB, duration_2_MSB, duration_2_LSB, duration_3_MSB, duration_3_LSB;
uint8_t accel_1_MSB, accel_1_LSB, accel_2_MSB, accel_2_LSB, accel_3_MSB, accel_3_LSB;

int axeXnow, axeYnow, axeZnow;
int oldX = 0, oldY = 0, oldZ = 0;

// MMA8452Q XYZ Axis data variables
int result[3];

bool deviceActive = false;
bool forwardMotion = false;
bool securityArmed = false;
bool appReady = false;

void ACC_INIT()
{
    // Put the accelerometer into standby mode
    I2C_SEND(ctrl_reg1, 0x00);
    delay(10);

    // 4G full range mode, B00000001 = 4G, B00000010 = 8G
    I2C_SEND(xyz_data_cfg, B00000001);
    delay(10);

    // Configure motion event detection in X,Y,Z axis
    I2C_SEND(motion_src_cfg, B11111000);
    delay(10);

    // B00011001 = 100Hz data rate, no auto wake, no auto scale adjust, no fast read mode
    I2C_SEND(ctrl_reg1, B00011001);
    delay(10);
}

void setup()
{
  // put your setup code here, to run once:
  pinMode(trigPin_1, OUTPUT);
  pinMode(echoPin_1, INPUT);
  pinMode(trigPin_2, OUTPUT);
  pinMode(echoPin_2, INPUT);
  pinMode(trigPin_3, OUTPUT);
  pinMode(echoPin_3, INPUT);

  pinMode(rpiSecurity, OUTPUT);
  pinMode(rpiWake, OUTPUT);
  pinMode(rpiStop, OUTPUT);

  digitalWrite(rpiSecurity, LOW);
  digitalWrite(rpiWake, LOW);
  digitalWrite(rpiStop, LOW);
  
  Wire.begin();
  Serial.begin(9600);

  ACC_INIT();
}

void loop()
{   
  // If device is actively streaming to phone
  if(deviceActive && appReady)
  {
    //Ultrasonic Sensor 1
    digitalWrite(trigPin_1, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin_1, LOW);
    duration_1 = pulseIn(echoPin_1, HIGH, 40000);
    delayMicroseconds(80);
  
    //Ultrasonic Sensor 2
    digitalWrite(trigPin_2, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin_2, LOW);
    duration_2 = pulseIn(echoPin_2, HIGH, 40000);
    delayMicroseconds(80);
  
    //Ultrasonic Sensor 3
    digitalWrite(trigPin_3, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin_3, LOW);
    duration_3 = pulseIn(echoPin_3, HIGH, 40000);
    delayMicroseconds(80);

    // Get orientation data from accelerometer, check for shutdown scenario, set flag if true
    getAccelData();
  
    // Transmit buffer to phone app
    sendDataToPhone();

    // Check to see if a stop command has been issued by the app if not already in shutdown
    if(deviceActive)
      checkRxBuffer();
  }

  // If not active, wait for wake command over serial, check twice a second
  else
  {
    checkRxBuffer();
    getAccelData();
  }
  
}

void checkRxBuffer()
{
  uint16_t bytesAvail = Serial.available();
  bool cmdFound = false;
  
  // Data is sitting in the rxBuffer
  if(bytesAvail > 0)
  {
    for(i = 0; i < bytesAvail; i++)
    {
      // Grab 1 byte from rxBuffer
      uint8_t cmd = Serial.read();

      switch(cmd)
      {
        // Start TM stream
        case 0x7F:
        {
          // First wake command
          if(!deviceActive)
          {
            deviceActive = true;

            // If security was armed, disarm it before turning system on
            if(securityArmed)
              securityArmed = false;
  
            digitalWrite(rpiWake, HIGH);
            delay(100);
            digitalWrite(rpiWake, LOW);
          }

          // Second wake command, ready for data stream
          else
            appReady = true;

          cmdFound = true;
          break;
        }

        // Arm security
        case 0x1C:
        {
          securityArmed = !securityArmed;
          cmdFound = true;
          break;
        }

        // Halt TM stream
        case 0x1B:
        {
          deviceActive = false;
          appReady = false;

          digitalWrite(rpiStop, HIGH);
          delay(100);
          digitalWrite(rpiStop, LOW);
          
          cmdFound = true;
          break;
        }

        // Start debug stream
        case 0x1A:
        {
          deviceActive = true;
          appReady = true;

          // If security was armed, disarm it before turning system on
          if(securityArmed)
            securityArmed = false;

          cmdFound = true;
          break;
        }

        default:
          cmdFound = false;
          break;
      }

      // Break loop if command is found in the buffer
      if(cmdFound)
        break;
    }
  }
  
}

//SEND data to MMA7660
void I2C_SEND(unsigned char REG_ADDRESS, unsigned char DATA)
{
  Wire.beginTransmission(adress_acc);
  Wire.write(REG_ADDRESS);
  Wire.write(DATA);
  Wire.endTransmission();
}

void I2C_READ_ACC(int ctrlreg_address)
{
  byte REG_ADDRESS[7];
  int accel[4];
  
  Wire.beginTransmission(adress_acc);
  Wire.write(ctrlreg_address);
  Wire.endTransmission();
  Wire.requestFrom(adress_acc, 7);

  for(i = 0; i < 7; i++) 
    REG_ADDRESS[i] = Wire.read();

  for (i=1; i<7; i=i+2)
  {
    accel[0] = (REG_ADDRESS[i+1] | ( (int) REG_ADDRESS[i] << 8) ) >> 6; // X
    
    if (accel[0] > 0x01FF)
      accel[1] = ( (~accel[0] + 1) - 0xFC00);
    
    else
      accel[1] = accel[0];

    if(i == 1)
        axeXnow=accel[1];
        
    else if(i == 3)
        axeYnow=accel[1];

    else if(i == 5)
        axeZnow=accel[1];
  }
  
}

void I2C_READ_REG(int ctrlreg_address) //READ number data from i2c slave ctrl-reg register and return the result in a vector
{
  unsigned char REG_ADDRESS;
  Wire.beginTransmission(adress_acc); //= ST + (Device Adress + W(0)) + wait for ACK
  Wire.write(ctrlreg_address);  // register to read
  Wire.endTransmission();
  Wire.requestFrom(adress_acc, 2); // read a number of byte and store them in write received
}

void sendDataToPhone()
{  
  // Transmit header + data + accelerometer X, Y, and Z
  Serial.write(0x1F);
    
  duration_1_MSB = (duration_1 & 0xFF00) >> 8;
  duration_1_LSB = (duration_1 & 0x00FF);
  Serial.write(duration_1_MSB);
  Serial.write(duration_1_LSB);
  
  duration_2_MSB = (duration_2 & 0xFF00) >> 8;
  duration_2_LSB = (duration_2 & 0x00FF);
  Serial.write(duration_2_MSB);
  Serial.write(duration_2_LSB);
  
  duration_3_MSB = (duration_3 & 0xFF00) >> 8;
  duration_3_LSB = (duration_3 & 0x00FF);
  Serial.write(duration_3_MSB);
  Serial.write(duration_3_LSB);

  accel_1_MSB = (axeXnow & 0xFF00) >> 8;
  accel_1_LSB = (axeXnow & 0x00FF);
  Serial.write(accel_1_MSB);
  Serial.write(accel_1_LSB);

  accel_2_MSB = (axeYnow & 0xFF00) >> 8;
  accel_2_LSB = (axeYnow & 0x00FF);
  Serial.write(accel_2_MSB);
  Serial.write(accel_2_LSB);

  accel_3_MSB = (axeZnow & 0xFF00) >> 8;
  accel_3_LSB = (axeZnow & 0x00FF);
  Serial.write(accel_3_MSB);
  Serial.write(accel_3_LSB);

//  // If going into shutdown, inform app with 0xBB
//  if(!deviceActive)
//  {
//    Serial.write(0xBB);
//    Serial.write(0xBB);
//    digitalWrite(rpiSecurity, HIGH);
//    delay(100);
//    digitalWrite(rpiSecurity, LOW);
//  }
  
}

void getAccelData()
{
  // Read data from accelerometer, send Z axis data
  I2C_READ_ACC(0x00);

  int diffZ = abs(axeZnow - oldZ);
//  Serial.print(axeZnow);
//  Serial.print(", diff: ");
//  Serial.println(diffZ);
    
  // Checks Z-axis for change in acceleration greater than 30
//  if(diffZ > 200 && oldZ > 0)
//    Serial.println("Sufficient motion detected");

  if(securityArmed)
    checkSecurity();
  
  oldX = axeXnow;
  oldY = axeYnow;
  oldZ = axeZnow;
}

void checkSecurity()
{
  bool triggered = false;
    
  if(axeXnow - oldX > 10 && oldX > 0 && !triggered)
  {
    digitalWrite(rpiSecurity, HIGH);
    delay(100);
    digitalWrite(rpiSecurity, LOW);
    triggered = true;
  }

  if(axeYnow - oldY > 10 && oldY > 0 && !triggered)
  {
    digitalWrite(rpiSecurity, HIGH);
    delay(100);
    digitalWrite(rpiSecurity, LOW);
    triggered = true;
  }

  if(axeZnow - oldZ > 10 && oldZ > 0 && !triggered)
  {
    digitalWrite(rpiSecurity, HIGH);
    delay(100);
    digitalWrite(rpiSecurity, LOW);
    triggered = true;
  }
  
}

