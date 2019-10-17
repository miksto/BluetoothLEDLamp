#include "GradientEffect.h"
#include "ColorUtils.h"
#include "Log.h"
#include "LedStrip.h"

GradientEffect::GradientEffect(LedStrip* strip, RgbColor color1, RgbColor color2)
  : LampEffect(strip, LampEffectId::gradient_effect, LampEffectEepromDataSize::gradient_effect) {
  this->color1 = color1;
  this->color2 = color2;
}

void GradientEffect::setup() {
  RgbwColor rgbwColor1 = ColorUtils::rgbToRgbw(this->color1);
  RgbwColor rgbwColor2 = ColorUtils::rgbToRgbw(this->color2);

  const int edgeBandLedCount = 30;

  for (int i = 0; i < edgeBandLedCount; i++) {
    this->strip->SetPixelColor(i, rgbwColor1);
  }

  for (int i = edgeBandLedCount; i < LedStripConstants::led_count - edgeBandLedCount; i++) {
    float progress = (i - edgeBandLedCount) / ((float) LedStripConstants::led_count - 2 * edgeBandLedCount);
    RgbwColor blend = RgbwColor::LinearBlend(color1, color2, progress);
    this->strip->SetPixelColor(i, blend);
  }

  for (int i = LedStripConstants::led_count - edgeBandLedCount; i < LedStripConstants::led_count; i++) {
    this->strip->SetPixelColor(i, rgbwColor2);
  }

  this->strip->Show();
}

void GradientEffect::next() {
  delay(50);
}

uint8_t* GradientEffect::toBytes() {
  uint8_t *bytes = new uint8_t[this->eepromDataSize];
  uint8_t* color1bytes = ColorUtils::rgbColorToBytes(this->color1);
  uint8_t* color2bytes = ColorUtils::rgbColorToBytes(this->color2);
  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[i] = color1bytes[i];
  }

  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[RGB_COLOR_BYTES_LENGTH + i] = color2bytes[i];
  }
  delete[] color1bytes;
  delete[] color2bytes;
  return bytes;
}

GradientEffect* GradientEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  RgbColor color1 = ColorUtils::bytesToRgbColor(bytes);
  RgbColor color2 = ColorUtils::bytesToRgbColor(&bytes[RGB_COLOR_BYTES_LENGTH]);
  GradientEffect* effect = new GradientEffect(strip, color1, color2);
  return effect;
}
