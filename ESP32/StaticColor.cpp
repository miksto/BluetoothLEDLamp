#include "StaticColor.h"

#include <NeoPixelBus.h>
#include "LedStrip.h"
#include "ColorUtils.h"
#include "Log.h"


StaticColor::StaticColor(LedStrip* strip, RgbColor color) : LampEffect(strip, LampEffectId::static_color, LampEffectEepromDataSize::static_color) {
  this->color = color;
}

void StaticColor::setup() {
  RgbwColor rgbwColor = ColorUtils::rgbToRgbw(this->color);
  this->strip->ClearTo(rgbwColor);
  this->strip->Show();
}

void StaticColor::next() {
  delay(40);
}

uint8_t* StaticColor::toBytes() {
  Log::logColor("Storing", this->color);
  uint8_t* bytes = ColorUtils::rgbColorToBytes(this->color);
  return bytes;
}

StaticColor* StaticColor::fromBytes(LedStrip* strip, uint8_t* bytes) {
  RgbColor rgb = ColorUtils::bytesToRgbColor(bytes);
  Log::logColor("LoadingColor", rgb);
  StaticColor* effect = new StaticColor(strip, rgb);
  return effect;
}
