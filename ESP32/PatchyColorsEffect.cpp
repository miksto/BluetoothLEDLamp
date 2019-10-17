#include "PatchyColorsEffect.h"
#include "ColorUtils.h"
#include "Log.h"
#include "LedStrip.h"

PatchyColorsEffect::PatchyColorsEffect(LedStrip* strip, RgbColor color1, RgbColor color2, RgbColor color3)
  : LampEffect(strip, LampEffectId::patchy_colors_effect, LampEffectEepromDataSize::patchy_colors_effect) {
  this->color1 = color1;
  this->color2 = color2;
  this->color3 = color3;
}

void PatchyColorsEffect::setup() {
  RgbwColor rgbwColor1 = ColorUtils::rgbToRgbw(this->color1);
  RgbwColor rgbwColor2 = ColorUtils::rgbToRgbw(this->color2);
  RgbwColor rgbwColor3 = ColorUtils::rgbToRgbw(this->color3);

  for(int i = 0; i < LedStripConstants::led_count; i++) {
    int randNum = rand() % 3;
    if (randNum == 0) {
      this->strip->SetPixelColor(i, rgbwColor1);
    } else if (randNum == 1) {
      this->strip->SetPixelColor(i, rgbwColor2);
    } else {
      this->strip->SetPixelColor(i, rgbwColor3);
    }
  }

  this->strip->Show();
}

void PatchyColorsEffect::next() {
  delay(50);
}

uint8_t* PatchyColorsEffect::toBytes() {
  uint8_t *bytes = new uint8_t[this->eepromDataSize];
  uint8_t* color1bytes = ColorUtils::rgbColorToBytes(this->color1);
  uint8_t* color2bytes = ColorUtils::rgbColorToBytes(this->color2);
  uint8_t* color3bytes = ColorUtils::rgbColorToBytes(this->color3);

  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[i] = color1bytes[i];
  }

  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[RGB_COLOR_BYTES_LENGTH + i] = color2bytes[i];
  }

  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[2 * RGB_COLOR_BYTES_LENGTH + i] = color3bytes[i];
  }
  delete[] color1bytes;
  delete[] color2bytes;
  delete[] color3bytes;
  return bytes;
}

PatchyColorsEffect* PatchyColorsEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  RgbColor color1 = ColorUtils::bytesToRgbColor(bytes);
  RgbColor color2 = ColorUtils::bytesToRgbColor(&bytes[RGB_COLOR_BYTES_LENGTH]);
  RgbColor color3 = ColorUtils::bytesToRgbColor(&bytes[2 * RGB_COLOR_BYTES_LENGTH]);
  PatchyColorsEffect* effect = new PatchyColorsEffect(strip, color1, color2, color3);
  return effect;
}
