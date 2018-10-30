//Test code for the Ultrasonic Sensors using
//the MSP430

#define trigPin_1 P4_2
#define echoPin_1 P4_3
#define trigPin_2 P2_4
#define echoPin_2 P2_2
#define trigPin_3 P3_4
#define echoPin_3 P3_5

#include <Wire.h>
#include <SparkFun_MMA8452Q.h>
#include <stdint.h>

MMA8452Q accel;

void printAccels();

uint8_t i = 0, duration_1_MSB, duration_1_LSB, duration_2_MSB, duration_2_LSB, duration_3_MSB, duration_3_LSB;
uint32_t duration_1, duration_2, duration_3, accel_x, accel_y, accel_z;


void setup()
{
  // put your setup code here, to run once:
  pinMode(trigPin_1, OUTPUT);
  pinMode(echoPin_1, INPUT);
  pinMode(trigPin_2, OUTPUT);
  pinMode(echoPin_2, INPUT);
  pinMode(trigPin_3, OUTPUT);
  pinMode(echoPin_3, INPUT);
  Serial.begin(9600);
  accel.init(SCALE_2G, ODR_6);
  Serial.println("Startup complete");
}

void loop()
{
  // put your main code here, to run repeatedly:
  
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

  sendUltraToPhone();
  //printUltra();

  if(accel.available())
  {
      accel.read();
      Serial.write( (uint16_t) accel.cx);
      Serial.write( (uint16_t) accel.cy);
      Serial.write( (uint16_t) accel.cz);
      printAccels();
  }

  //delay(500);
}

void printAccels()
{
  Serial.print(accel.cx, 3);
  Serial.print("\t");
  Serial.print(accel.cy, 3);
  Serial.print("\t");
  Serial.print(accel.cz, 3);
  Serial.print("\t");
  Serial.println("\n");
}

void printUltra()
{
  Serial.print("Ultrasonic_1: ");
  Serial.print(duration_1/58);
  Serial.print("\t");

  Serial.print("Ultrasonic_2: ");
  Serial.print(duration_2/58);
  Serial.print("\t");

  Serial.print("Ultrasonic_3: ");
  Serial.print(duration_3/58);
  Serial.print("\t");
  Serial.println();
}

void sendUltraToPhone()
{
  Serial.write(0x1F);
  
  duration_1_MSB = (duration_1 & 0xFF00) >> 8;
  duration_1_LSB = (duration_1 & 0x00FF);
  Serial.write(duration_1_MSB);
  Serial.write(duration_1_LSB);
  //Serial.write('\n');
  
  duration_2_MSB = (duration_2 & 0xFF00) >> 8;
  duration_2_LSB = (duration_2 & 0x00FF);
  Serial.write(duration_2_MSB);
  Serial.write(duration_2_LSB);
  //Serial.write('\n');
  
  duration_3_MSB = (duration_3 & 0xFF00) >> 8;
  duration_3_LSB = (duration_3 & 0x00FF);
  Serial.write(duration_3_MSB);
  Serial.write(duration_3_LSB);
  //Serial.write('\n');
}
