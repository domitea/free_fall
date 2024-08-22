#include <WiFi.h>
#include <WiFiUdp.h>
#include <coap-simple.h>
#include <Adafruit_MAX1704X.h>
#include <Adafruit_BNO08x.h>

const char* ssid     = "SSID";
const char* password = "PASS";
const IPAddress server_address = IPAddress(192,168,100,200);

float accelerometer[3];
float gyro[3]; 

#define BNO08X_RESET -1

// CoAP client response callback
void callback_response(CoapPacket &packet, IPAddress ip, int port);

// UDP and CoAP class
// other initialize is "Coap coap(Udp, 512);"
// 2nd default parameter is COAP_BUF_MAX_SIZE(defaulit:128)
// For UDP fragmentation, it is good to set the maximum under
// 1280byte when using the internet connection.
WiFiUDP udp;
Coap coap(udp);

Adafruit_MAX17048 maxlipo;

Adafruit_BNO08x bno08x(BNO08X_RESET);
sh2_SensorValue_t sensorValue;

// CoAP client response callback
void callback_response(CoapPacket &packet, IPAddress ip, int port) {
  //Serial.println("[Coap Response got]");
  
  char p[packet.payloadlen + 1];
  memcpy(p, packet.payload, packet.payloadlen);
  p[packet.payloadlen] = NULL;
  
  //Serial.println(p);
}

void setup() {
  //Serial.begin(115200);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
  }

  while (!maxlipo.begin()) {
    //Serial.println(F("Couldnt find Adafruit MAX17048?\nMake sure a battery is plugged in!"));
  }

  // Try to initialize!
  if (!bno08x.begin_I2C()) {
    // if (!bno08x.begin_UART(&Serial1)) {  // Requires a device with > 300 byte
    // UART buffer! if (!bno08x.begin_SPI(BNO08X_CS, BNO08X_INT)) {
    //Serial.println("Failed to find BNO08x chip");
    while (1) {
      delay(10);
    }
  }

  if (!bno08x.enableReport(SH2_ACCELEROMETER)) {
    //Serial.println("Could not enable accelerometer");
  }
  if (!bno08x.enableReport(SH2_GYROSCOPE_CALIBRATED)) {
    //Serial.println("Could not enable gyroscope");
  }

  // LED State
  pinMode(13, OUTPUT);

  // client response callback.
  // this endpoint is single callback.
  //Serial.println("Setup Response Callback");
  coap.response(callback_response);

  // start coap server/client
  coap.start();

  // if nothing is broke, led will light up
  digitalWrite(13, HIGH);
}

void loop() {
  delay(10);
  coap.loop();

  if (bno08x.wasReset()) {
    //Serial.print("sensor was reset ");
    if (!bno08x.enableReport(SH2_ACCELEROMETER)) {
    //Serial.println("Could not enable accelerometer");
    }
    if (!bno08x.enableReport(SH2_GYROSCOPE_CALIBRATED)) {
      //Serial.println("Could not enable gyroscope");
    }
  }

  if (!bno08x.getSensorEvent(&sensorValue)) {
    return;
  }

  switch (sensorValue.sensorId) {

    case SH2_ACCELEROMETER:
      accelerometer[0] = sensorValue.un.accelerometer.x;
      accelerometer[1] = sensorValue.un.accelerometer.y;
      accelerometer[2] = sensorValue.un.accelerometer.z;
      break;
    case SH2_GYROSCOPE_CALIBRATED:
      gyro[0] = sensorValue.un.gyroscope.x;
      gyro[1] = sensorValue.un.gyroscope.y;
      gyro[2] = sensorValue.un.gyroscope.z;
      break;
  }
  const char* message = ("[" + String(gyro[0]) + "," + String(gyro[1]) + ","+ String(gyro[2])+ "]").c_str();

  coap.put(server_address, 5683, "gyro", message);

  const char* acc_message = ("[" + String(accelerometer[0]) + "," + String(accelerometer[1]) + ","+ String(accelerometer[2])+ "]").c_str();

  coap.put(server_address, 5683, "acc", acc_message);    
}