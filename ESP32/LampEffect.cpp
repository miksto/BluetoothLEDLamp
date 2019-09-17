#include "LampEffect.h"
#include "StaticColor.h"
#include "BeaconLight.h"
#include "ColorLoop.h"
#include "RotatingLines.h"
#include "RotatingRainbow.h"

LampEffect* LampEffect::createEffect(LedStrip* strip, uint8_t effectId, uint8_t* bytes, uint8_t dataSize) {
  switch (effectId) {
    case LampEffectId::static_color: return StaticColor::fromBytes(strip, effectId, bytes, dataSize);

    case LampEffectId::beacon_light: return new BeaconLight(strip);

    case LampEffectId::color_loop: return new ColorLoop(strip);

    case LampEffectId::rotating_lines: return new RotatingLines(strip);

    case LampEffectId::rotating_rainbow: return new RotatingRainbow(strip);
  }
}

uint8_t LampEffect::dataSizeForEffectId(uint8_t effectId) {
  switch (effectId) {
    case LampEffectId::static_color: return LampEffectEepromDataSize::static_color;

    case LampEffectId::beacon_light: return LampEffectEepromDataSize::beacon_light;

    case LampEffectId::color_loop: return LampEffectEepromDataSize::color_loop;

    case LampEffectId::rotating_lines: return LampEffectEepromDataSize::rotating_lines;

    case LampEffectId::rotating_rainbow: return LampEffectEepromDataSize::rotating_rainbow;
  }
}
