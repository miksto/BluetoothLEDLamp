#include "LampEffect.h"
#include "StaticColor.h"
#include "BeaconLight.h"
#include "ColorLoop.h"
#include "RotatingLines.h"
#include "RotatingRainbow.h"
#include "GlimmeringEffect.h"
#include "SunsetEffect.h"

LampEffect* LampEffect::createEffect(LedStrip* strip, uint8_t* bytes) {
  uint8_t effectId = bytes[0];
  uint8_t dataSize = dataSizeForEffectId(effectId);
  uint8_t* effectData;

  if (dataSize > 0) {
    effectData = &bytes[1];
  } else {
    effectData = nullptr;
  }
  
  Serial.print("Effect Id:");
  Serial.println(effectId);

  switch (effectId) {
    case LampEffectId::static_color: return StaticColor::fromBytes(strip, effectData);

    case LampEffectId::beacon_light: return new BeaconLight(strip);

    case LampEffectId::color_loop: return new ColorLoop(strip);

    case LampEffectId::rotating_lines: return new RotatingLines(strip);

    case LampEffectId::rotating_rainbow: return RotatingRainbow::fromBytes(strip, effectData);

    case LampEffectId::glimmering_effect: return GlimmeringEffect::fromBytes(strip, effectData);

    case LampEffectId::sunset_effect: return SunsetEffect::fromBytes(strip, effectData);
  }
  Serial.print("Invalid effectId: ");
  Serial.println(effectId);
}

//StaticColor* LampEffect::createStaticColorEffect(LedStrip* strip, uint8_t* bytes) {
//  return StaticColor::fromBytes(strip, bytes[1]);
//}

uint8_t LampEffect::dataSizeForEffectId(uint8_t effectId) {
  switch (effectId) {
    case LampEffectId::static_color: return LampEffectEepromDataSize::static_color;

    case LampEffectId::beacon_light: return LampEffectEepromDataSize::beacon_light;

    case LampEffectId::color_loop: return LampEffectEepromDataSize::color_loop;

    case LampEffectId::rotating_lines: return LampEffectEepromDataSize::rotating_lines;

    case LampEffectId::rotating_rainbow: return LampEffectEepromDataSize::rotating_rainbow;

    case LampEffectId::glimmering_effect: return LampEffectEepromDataSize::glimmering_effect;

    case LampEffectId::sunset_effect: return LampEffectEepromDataSize::sunset_effect;
  }
}
