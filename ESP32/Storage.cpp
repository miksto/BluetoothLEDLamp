#include "Storage.h"
#include "ColorUtils.h"
#include "LedStrip.h"

#include <EEPROM.h>

#define ADDR_EFFECT_ID 0
#define ADDR_DATA 1

#define STATIC_COLOR_INDEX 0
#define EFFECT_INDEX 4

#define EEPROM_SIZE 20


namespace Storage
{

void init() {
  EEPROM.begin(EEPROM_SIZE);
}

void saveEffect(LampEffect* effect, uint8_t start_index) {
  Serial.println("Saving effect");
  EEPROM.write(start_index + ADDR_EFFECT_ID, effect->id);

  if (effect->eepromDataSize > 0) {
    uint8_t* bytes = effect->toBytes();
    Serial.println("saveEffect");
    for (int i = 0; i < effect->eepromDataSize; i++) {
      EEPROM.write(start_index + ADDR_DATA + i, bytes[i]);
      Serial.println(bytes[i]);
    }
  }
  EEPROM.commit();
}

LampEffect* loadEffect(LedStrip* strip, uint8_t start_index) {
  uint8_t effectId = EEPROM.read(start_index + ADDR_EFFECT_ID);
  uint8_t eepromDataSize = LampEffect::dataSizeForEffectId(effectId);
  Serial.print("Loading effect with byte size: ");
  Serial.println(eepromDataSize);

  uint8_t* bytes;
  bytes = new uint8_t[eepromDataSize + 1];
  Serial.println("loadEffect");
  for (int i = 0; i < eepromDataSize + 1; i++) {
    bytes[i] = EEPROM.read(start_index + ADDR_EFFECT_ID + i);
    Serial.println(bytes[i]);
  }

  LampEffect* lampEffect = LampEffect::createEffect(strip, bytes);
  Serial.println("lampEffect created");
  delete bytes;
  return lampEffect;
}

void saveStaticColorEffect(StaticColor* effect) {
  saveEffect(effect, STATIC_COLOR_INDEX);
}

StaticColor* loadStaticColorEffect(LedStrip* strip) {
  return (StaticColor*) loadEffect(strip, STATIC_COLOR_INDEX);
}

void saveEffect(LampEffect* effect) {
  saveEffect(effect, EFFECT_INDEX);
}

LampEffect* loadEffect(LedStrip* strip) {
  return loadEffect(strip, EFFECT_INDEX);
}

};
