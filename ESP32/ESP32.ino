#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <NeoPixelBus.h>

#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID_COLOR "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define LED_PIN 18
#define LED_COUNT 121

NeoPixelBus<NeoGrbwFeature, Neo800KbpsMethod> strip(LED_COUNT, LED_PIN);

RgbwColor hslToRgbw(HslColor hsl) {
  RgbwColor rgbw(hsl);
  uint8_t smallestValue = 255;
  smallestValue = min(smallestValue, rgbw.R);
  smallestValue = min(smallestValue, rgbw.G);
  smallestValue = min(smallestValue, rgbw.B);
  rgbw.R -= smallestValue;
  rgbw.G -= smallestValue;
  rgbw.B -= smallestValue;
  rgbw.W = smallestValue;
  return rgbw;
}

void setColor(std::string bytes) {
  int color = (bytes[3] << 8) | bytes[2];
  float hslH = (float)color/65536;
  float hslS =  (float) bytes[1]/255;
  float hslL = (float) bytes[0]/255;

  HslColor hslColor(hslH, hslS, hslL);
  RgbwColor rgbw = hslToRgbw(hslColor);
  
  for (int i = 0; i < LED_COUNT; i++) {
    strip.SetPixelColor(i, rgbw);
  }
  strip.Show();
}

class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string value = pCharacteristic->getValue();
      setColor(value);
    }
};


void setup() {
  Serial.begin(115200);
  strip.Begin();
  strip.Show();

  BLEDevice::init("LedLAMP");
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  BLECharacteristic *pCharacteristic = pService->createCharacteristic(
                                         CHARACTERISTIC_UUID_COLOR,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  pCharacteristic->setCallbacks(new MyCallbacks());
  pService->start();

  BLEAdvertising *pAdvertising = pServer->getAdvertising();
  pAdvertising->start();
}

void loop() {
  // put your main code here, to run repeatedly:
  delay(2000);
}
