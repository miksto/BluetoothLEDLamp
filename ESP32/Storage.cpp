#include "Storage.h"
#include "ColorUtils.h"
#include "LedStrip.h"

#include <EEPROM.h>

#define ADDR_EFFECT_ID 0
#define ADDR_DATA 1

#define ADDR_LAST_COLOR = 10

#define EEPROM_SIZE 20


namespace Storage
{

void init() {
  EEPROM.begin(EEPROM_SIZE);
}

void saveEffect(LampEffect* effect) {
  Serial.println("Saving effect");
  EEPROM.write(ADDR_EFFECT_ID, effect->id);

  if (effect->eepromDataSize > 0) {
    uint8_t* bytes = effect->toBytes();
    for (int i = 0; i < effect->eepromDataSize; i++) {
      EEPROM.write(ADDR_DATA + i, bytes[i]);
    }
  }
  EEPROM.commit();
}

LampEffect* loadEffect(LedStrip* strip) {
  Serial.println("Loading effect");
  uint8_t effectId = EEPROM.read(ADDR_EFFECT_ID);
  uint8_t eepromDataSize = LampEffect::dataSizeForEffectId(effectId);

  uint8_t* bytes;
  if (eepromDataSize > 0) {
    bytes = new uint8_t[eepromDataSize];
    
    for (int i = 0; i < eepromDataSize; i++) {
      bytes[i] = EEPROM.read(ADDR_DATA + i);
    }
  } else {
    bytes = nullptr;
  }

  LampEffect* lampEffect = LampEffect::createEffect(strip, effectId, bytes, eepromDataSize);
  delete bytes;
  return lampEffect;
}

void saveStaticColorEffect(StaticColor* effect) {
  saveEffect(effect);
}

StaticColor* loadStaticColorEffect(LedStrip* strip) {
  return (StaticColor*) loadEffect(strip);
}

};
