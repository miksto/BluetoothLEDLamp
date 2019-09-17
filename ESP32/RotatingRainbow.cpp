#include "RotatingRainbow.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>

RotatingRainbow::RotatingRainbow(LedStrip* strip) : LampEffect(strip, LampEffectId::rotating_rainbow, LampEffectEepromDataSize::rotating_rainbow) {

}

void RotatingRainbow::setup() {
  for (int i = 0; i< LedStripConstants::led_count; i++) {
      HslColor color = HslColor(i*(1.0/LedStripConstants::led_count), 1, 0.5);
      strip->SetPixelColor(i, color);
  }
  strip->Show();
}

void RotatingRainbow::next() {
  strip->RotateLeft(1);
  strip->Show();
  delay(40);
}

uint8_t* RotatingRainbow::toBytes() { return nullptr; }
