#include "SunsetEffect.h"
#include "LedStrip.h"

SunsetEffect::SunsetEffect(LedStrip* strip, int timeDurationMinutes, float visibleColorInterval, float startHueValue, float endHueValue)
  : LampEffect(strip, LampEffectId::sunset_effect, LampEffectEepromDataSize::sunset_effect) {
  this->timeDurationMinutes = timeDurationMinutes;
  this->visibleColorInterval = visibleColorInterval;
  this->startHueValue = startHueValue;
  this->endHueValue = endHueValue;
  this->hueDistance = startHueValue + (1 - endHueValue);
}

void SunsetEffect::applyGradient(float startHueValue) {
  for (int i = 0; i < LedStripConstants::led_count; i++) {
    float progress = (i / (float) LedStripConstants::led_count);
    float currentHueValue = startHueValue - progress * this->visibleColorInterval;
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


void SunsetEffect::setup() {
  this->startTime = millis();
}

void SunsetEffect::next() {
  float progress = (millis() - this->startTime) / (float) (this->timeDurationMinutes * 60000);
  if (progress >= 1) {
    this->startTime = millis();
    progress = 0;
  }
  float currentHue = this->startHueValue - progress * this->hueDistance;
  applyGradient(currentHue);
  delay(200);
}

uint8_t* SunsetEffect::toBytes() {
  uint8_t *bytes = new uint8_t[this->eepromDataSize];
  bytes[0] = this->timeDurationMinutes;
  bytes[1] = (int) (this->visibleColorInterval * 255);
  bytes[2] = (int) (this->startHueValue * 255);
  bytes[3] = (int) (this->endHueValue * 255);
  return bytes;
}

SunsetEffect* SunsetEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  int duration = bytes[0];
  float colorInterval = bytes[1] / 255.0f;
  float startHue = bytes[2] / 255.0f;
  float endHue = bytes[3] / 255.0f;

  SunsetEffect* effect = new SunsetEffect(strip, duration, colorInterval, startHue, endHue);
  return effect;
}
