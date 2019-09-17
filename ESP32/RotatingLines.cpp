#include "RotatingLines.h"
#include "LedStrip.h"


RotatingLines::RotatingLines(LedStrip* strip) : LampEffect(strip, LampEffectId::rotating_lines, LampEffectEepromDataSize::rotating_lines) {

}

void RotatingLines::setup() {
  RgbwColor white(80, 0, 0, 0);
  RgbwColor blue(0, 80, 0, 0);
  RgbwColor yellow(0, 0, 80, 0);
  RgbwColor clr(0, 80, 80, 0);
  
  for (int i = 0; i < LedStripConstants::led_count-4; i+=4) {
    strip->SetPixelColor(i, white);
    strip->SetPixelColor(i+1, blue);
    strip->SetPixelColor(i+2, yellow);
    strip->SetPixelColor(i+3, clr);
  }
  strip->Show();
}

void RotatingLines::next() {
  strip->RotateRight(1);
  strip->Show();
  delay(100);
}

uint8_t* RotatingLines::toBytes() { return nullptr; }
