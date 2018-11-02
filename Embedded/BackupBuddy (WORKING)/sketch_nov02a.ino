//Test code for the Ultrasonic Sensors using
//the MSP430

#define trigPin_1 P4_2
#define echoPin_1 P4_3
#define trigPin_2 P2_4
#define echoPin_2 P2_2
#define trigPin_3 P1_4
#define echoPin_3 P1_3

#include <Wire.h>
#include <stdint.h>

// MMA8452Q Address, single slave on the bus
int adress_acc=0x1D;

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

// MMA8452Q XYZ Axis data variables
int result[3];
int axeXnow;
int axeYnow;
int axeZnow;

// Small counter variable
uint8_t i = 0;

bool s1Active = false, s2Active = false, s3Active = false;

union
{
  uint16_t data[5];
  uint8_t byteBuffer[10];
} txBuffer;

void ACC_INIT()
{
    // Put the accelerometer into standby mode
    I2C_SEND(ctrl_reg1 ,0x00);
    delay(10);

    // 2G full range mode
    I2C_SEND(xyz_data_cfg ,B00000000);
    delay(1);

    // B00000001 = 4G, B00000010 = 8G
    
    I2C_SEND(ctrl_reg1 ,B00000001);
    delay(1);

    // B00000001 = 800Hz data rate, no auto wake, no auto scale adjust, no fast read mode
    // B00100001 = 200Hz data rate, no auto wake, no auto scale adjust, no fast read mode
    // B01000001 = 50Hz data rate, no auto wake, no auto scale adjust, no fast read mode
    // B01110001 = 1.5Hz data rate, no auto wake, no auto scale adjust, no fast read mode
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

  attachInterrupt(echoPin_1, getDuration, digitalRead(echoPin_1)?FALLING:RISING);
  attachInterrupt(echoPin_2, getDuration, digitalRead(echoPin_2)?FALLING:RISING);
  attachInterrupt(echoPin_3, getDuration, digitalRead(echoPin_3)?FALLING:RISING);
  
  Wire.begin(); // start of the i2c protocol
  Serial.begin(9600);

  ACC_INIT();
  Serial.println("Startup complete");
}

void loop()
{ 
  // Attach header to buffer
  txBuffer.data[0] = 0x1F1F;

    
  
  //Ultrasonic Sensor 1
  digitalWrite(trigPin_1, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_1, LOW);
  txBuffer.data[1] = pulseIn(echoPin_1, HIGH, 40000);

  //Ultrasonic Sensor 2
  digitalWrite(trigPin_2, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_2, LOW);
  txBuffer.data[2] = pulseIn(echoPin_2, HIGH, 40000);

  //Ultrasonic Sensor 3
  digitalWrite(trigPin_3, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_3, LOW);
  txBuffer.data[3] = pulseIn(echoPin_3, HIGH, 40000);

  // Read data from accelerometer, send Z axis data
  I2C_READ_ACC(0x00);
  txBuffer.data[4] = (uint16_t) axeZnow;

  // Transmit buffer to phone app
  sendDataToPhone();
}

//SEND data to MMA7660
void I2C_SEND(unsigned char REG_ADDRESS, unsigned char DATA)
{
  Wire.beginTransmission(adress_acc);
  Wire.write(REG_ADDRESS);
  Wire.write(DATA);
  Wire.endTransmission();
}


void I2C_READ_ACC(int ctrlreg_address) //READ number data from i2c slave ctrl-reg register and return the result in a vector
{
  byte REG_ADDRESS[7];
  int accel[4];
  Wire.beginTransmission(adress_acc); //=ST + (Device Adress+W(0)) + wait for ACK
  Wire.write(ctrlreg_address);  // store the register to read in the buffer of the wire library
  Wire.endTransmission(); // actually send the data on the bus -note: returns 0 if transmission OK-
  Wire.requestFrom(adress_acc, 7); // read a number of byte and store them in wire.read (note: by nature, this is called an "auto-increment register adress")

  // 7 because on datasheet p.19 if FREAD=0, on auto-increment, the adress is shifted
  // according to the datasheet, because it's shifted, outZlsb are in adress 0x00
  // so we start reading from 0x00, forget the 0x01 which is now "status" and make the adapation by ourselves
  //this gives:
  //0 = status
  //1= X_MSB
  //2= X_LSB
  //3= Y_MSB
  //4= Y_LSB
  //5= Z_MSB
  //6= Z_LSB
  
  for(i = 0; i < 7; i++) 
  {
    REG_ADDRESS[i]=Wire.read(); //each time you read the write.read it gives you the next byte stored. The couter is reset on requestForm
//    Serial.write(REG_ADDRESS[i]);
  }

  // MMA8653FC gives the answer on 10bits. 8bits are on _MSB, and 2 are on _LSB
  // this part is used to concatenate both, and then put a sign on it (the most significant bit is giving the sign)
  // the explanations are on p.14 of the 'application notes' given by freescale.
  for (i=1;i<7;i=i+2)
  {
    accel[0] = (REG_ADDRESS[i+1]|((int)REG_ADDRESS[i]<<8))>>6; // X
    
    if (accel[0] > 0x01FF)
      accel[1] = ( (~accel[0] + 1) - 0xFC00); // note: with signed int, this code is optional
    
    else
      accel[1] = accel[0]; // note: with signed int, this code is optional
      
    switch(i)
    {
      case 1: 
        axeXnow=accel[1];
        break;
        
      case 3: 
        axeYnow=accel[1];
        break;
        
      case 5: 
        axeZnow=accel[1];
        break;
    }
  }
  
}

void I2C_READ_REG(int ctrlreg_address) //READ number data from i2c slave ctrl-reg register and return the result in a vector
{
  unsigned char REG_ADDRESS;
  Wire.beginTransmission(adress_acc); //= ST + (Device Adress+W(0)) + wait for ACK
  Wire.write(ctrlreg_address);  // register to read
  Wire.endTransmission();
  Wire.requestFrom(adress_acc, 1); // read a number of byte and store them in write received
}

void sendDataToPhone()
{
  // Transmit header + data + accelerometer Z
  Serial.write(txBuffer.byteBuffer, 10);
}

void getDuration()
{
  if(!s1Active)
  {
    s1Active = true;
  }

  if(!s2Active)
  {
    s2Active = true;
  }

  if(!s3Active)
  {
    s3Active = true;
  }
  
}

