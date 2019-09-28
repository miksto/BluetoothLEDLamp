#include "GlimmeringEffect.h"
#include "ColorUtils.h"
#include "Log.h"

GlimmeringEffect::GlimmeringEffect(LedStrip* strip, RgbColor primaryColor, RgbColor secondaryColor) : LampEffect(strip, LampEffectId::glimmering_effect, LampEffectEepromDataSize::glimmering_effect) {
  this->primaryColor = primaryColor;
  this->secondaryColor = secondaryColor;
}

void GlimmeringEffect::setup() {
  RgbwColor rgbwPrimaryColor = ColorUtils::rgbToRgbw(this->primaryColor);
  RgbwColor rgbwSecondaryColor = ColorUtils::rgbToRgbw(this->secondaryColor);

  this->strip->ClearTo(rgbwPrimaryColor);

  for (int i = 0; i < LedStripConstants::led_count; i++) {
    if ((rand() % 10) + 1 <= 2) {
      strip->SetPixelColor(i, rgbwSecondaryColor);
    }
  }

  this->strip->Show();
}

void GlimmeringEffect::next() {
  delay(50);
}

uint8_t* GlimmeringEffect::toBytes() {
  uint8_t *bytes = new uint8_t[this->eepromDataSize];
  uint8_t* color1bytes = ColorUtils::rgbColorToBytes(this->primaryColor);
  uint8_t* color2bytes = ColorUtils::rgbColorToBytes(this->secondaryColor);
  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[i] = color1bytes[i];
  }

  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[RGB_COLOR_BYTES_LENGTH + i] = color2bytes[i];
  }
  delete color1bytes;
  delete color2bytes;
  return bytes;
}

GlimmeringEffect* GlimmeringEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  RgbColor primaryColor = ColorUtils::bytesToRgbColor(bytes);
  RgbColor secondaryColor = ColorUtils::bytesToRgbColor(&bytes[RGB_COLOR_BYTES_LENGTH]);
  GlimmeringEffect* effect = new GlimmeringEffect(strip, primaryColor, secondaryColor);
  return effect;
}
