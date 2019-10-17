#include "PixelControlEffect.h"
#include "LedStrip.h"
#include "Log.h"


PixelControlEffect::PixelControlEffect(LedStrip* strip, RgbwColor* pixels, uint8_t pixelCount) : LampEffect(strip, LampEffectId::pixel_control_effect, LampEffectEepromDataSize::pixel_control_effect) {
  this->pixels = pixels;
  this->pixelCount = pixelCount;
}

void PixelControlEffect::setup() {
  this->strip->ClearTo(RgbwColor(100,100,0,4));
  for (int i = 0; i < this->pixelCount; i++) {
    this->strip->SetPixelColor(i, this->pixels[i]);
  }
  this->strip->Show();
}

void PixelControlEffect::next() {
  delay(40);
}

uint8_t* PixelControlEffect::toBytes() {
  return nullptr;
}

PixelControlEffect* PixelControlEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  uint8_t pixelCount = bytes[0];
  RgbwColor* pixels = new RgbwColor[pixelCount];
  for (int i = 0; i < pixelCount; i++) {
    pixels[i] = ColorUtils::rgbToRgbw(
                  ColorUtils::bytesToRgbColor(&bytes[i * RGB_COLOR_BYTES_LENGTH + 1])
                );
  }
  PixelControlEffect* effect = new PixelControlEffect(strip, pixels, pixelCount);
  return effect;
}
