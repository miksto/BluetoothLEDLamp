#include "StroboscopeEffect.h"
#include "LedStrip.h"


StroboscopeEffect::StroboscopeEffect(LedStrip* strip) : LampEffect(strip, LampEffectId::stroboscope_effect, LampEffectEepromDataSize::stroboscope_effect) {

}

void StroboscopeEffect::setup() {
}

void StroboscopeEffect::next() {
  uint8_t r = (rand() % 2) * 255;
  uint8_t g = (rand() % 2) * 255;
  uint8_t b = (rand() % 2) * 255;
  if (r == 0 && g == 0 && b == 0) {
    r = g = b = 255;
  }
  RgbwColor rgbwColor = ColorUtils::rgbToRgbw(RgbColor(r, g, b));
  strip->ClearTo(rgbwColor);
  strip->Show();
  delay(50);
  strip->ClearTo(RgbwColor(0, 0, 0, 0));
  strip->Show();
  delay(80);
}

uint8_t* StroboscopeEffect::toBytes() {
  return nullptr;
}
