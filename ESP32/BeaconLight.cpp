#include "BeaconLight.h"
#include "LedStrip.h"


BeaconLight::BeaconLight(LedStrip* strip) : LampEffect(strip, LampEffectId::beacon_light, LampEffectEepromDataSize::beacon_light) {

}

void BeaconLight::displayArray() {
  int maxIndex = this->arrayPos + LedStripConstants::led_count;

  for (int i = 0; i < LedStripConstants::led_count; i++) {
    int colorIndex = (i + this->arrayPos) % this->colorArrayLength;
    RgbwColor color = *(this->colorArray[colorIndex]);
    strip->SetPixelColor(i, color);
  }
  strip->Show();
}

void BeaconLight::setup() {
  RgbwColor* blue = new RgbwColor(0, 0, 100, 0);
  RgbwColor* red = new RgbwColor(100, 0, 0, 0);


  for (int i = 0; i < this->colorArrayLength;) {
    for (int j = 0; j < 4; j++, i++) {
      this->colorArray[i] = blue;
    }
    for (int j = 0; j < 4; j++, i++) {
      this->colorArray[i] = red;
    }
    for (int j = 0; j < 4; j++, i++) {
      this->colorArray[i] = blue;
    }
    for (int j = 0; j < 3; j++, i++) {
      this->colorArray[i] = red;
    }
    for (int j = 0; j < 4; j++, i++) {
      this->colorArray[i] = blue;
    }
    for (int j = 0; j < 3; j++, i++) {
      this->colorArray[i] = red;
    }
  }

  displayArray();

}

void BeaconLight::next() {
  this->arrayPos++;
  this->arrayPos %= this->colorArrayLength;
  displayArray();
  delay(50);
}

uint8_t* BeaconLight::toBytes() { return nullptr; }
