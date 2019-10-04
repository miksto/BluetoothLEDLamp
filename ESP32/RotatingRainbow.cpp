#include "RotatingRainbow.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>

RotatingRainbow::RotatingRainbow(LedStrip* strip, float colorInterval, float speed)
  : LampEffect(strip, LampEffectId::rotating_rainbow, LampEffectEepromDataSize::rotating_rainbow) {
  this->colorInterval = colorInterval;
  this->speed = speed;
  this->hueValue = 0;
}

void RotatingRainbow::applyGradient() {
  for (int i = 0; i < LedStripConstants::led_count; i++) {
    float progress = 1 - (i / (float) LedStripConstants::led_count);
    float currentHueValue = this->hueValue + progress * this->colorInterval;
    if (currentHueValue > 1) {
      currentHueValue -= 1;
    }
    if (currentHueValue < 0) {
      currentHueValue += 1;
    }
    HslColor color = HslColor(currentHueValue, 1, 0.4);
    this->strip->SetPixelColor(i, color);
  }
  this->strip->Show();
}

void RotatingRainbow::setup() {
  applyGradient();
}

void RotatingRainbow::next() {
  float delta = 0.1f * this->speed;
  this->hueValue -= delta;
  if (this->hueValue < 0) {
    this->hueValue += 1;
  }
  applyGradient();
  delay(40);
}

uint8_t* RotatingRainbow::toBytes() {
  uint8_t *bytes = new uint8_t[this->eepromDataSize];
  bytes[0] = (int) (this->colorInterval * 255);
  bytes[1] = (int) (this->speed * 255);
  return bytes;
}

RotatingRainbow* RotatingRainbow::fromBytes(LedStrip* strip, uint8_t* bytes) {
  float colorInterval = bytes[0] / 255.0f;
  float speed = bytes[1] / 255.0f;
  return new RotatingRainbow(strip, colorInterval, speed);
}
