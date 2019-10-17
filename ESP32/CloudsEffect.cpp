#include "CloudsEffect.h"
#include "ColorUtils.h"
#include "Log.h"
#include "LedStrip.h"

CloudsEffect::CloudsEffect(LedStrip* strip, RgbColor color1, RgbColor color2)
  : LampEffect(strip, LampEffectId::clouds_effect, LampEffectEepromDataSize::clouds_effect) {
  this->color1 = color1;
  this->color2 = color2;
}

void CloudsEffect::setup() {
  RgbwColor rgbwColor1 = ColorUtils::rgbToRgbw(this->color1);
  RgbwColor rgbwColor2 = ColorUtils::rgbToRgbw(this->color2);
  
  for (int i = 0; i < LedStripConstants::led_count;) {
    int count = rand() % 7;
    RgbwColor currentColor = (rand() % 3) == 0 ? rgbwColor1 : rgbwColor2;
    for (int j = 0; j < count; j++, i++) {
      this->strip->SetPixelColor(i, currentColor);
    }
  }
  Serial.println("setup()");
  this->strip->Show();
}

void CloudsEffect::next() {
  this->strip->RotateRight(7);
  this->strip->Show();
  delay(80);
}

uint8_t* CloudsEffect::toBytes() {
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

CloudsEffect* CloudsEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  RgbColor color1 = ColorUtils::bytesToRgbColor(bytes);
  RgbColor color2 = ColorUtils::bytesToRgbColor(&bytes[RGB_COLOR_BYTES_LENGTH]);
  CloudsEffect* effect = new CloudsEffect(strip, color1, color2);
  return effect;
}
