//Test code for the Ultrasonic Sensors using
//the MSP430

#define trigPin 12
#define echoPin 13
//#define LED 14
#define LED RED_LED
float duration = 0.0, distance = 0.0;
bool isOn = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(LED, OUTPUT);
  
  digitalWrite(LED, LOW);
}

void loop() {
  // put your main code here, to run repeatedly: 
  if(isOn)
    digitalWrite(LED, LOW);

  digitalWrite(trigPin, HIGH);
  delay(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);
  delay(80);  

  //The distance is in inches
  distance = duration / 296;

  Serial.println(distance);

  if(distance <= 10)
  {
    digitalWrite(LED, HIGH);
    isOn = !isOn;
    
    distance = 0.0;
  }

  else
  {
    digitalWrite(LED, LOW);
  }

  //Need to print the duration, which should change every second
  //depending on what is in front of the sensor
  
  
}