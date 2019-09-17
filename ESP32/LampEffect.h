#ifndef LAMP_EFFECT_H
#define LAMP_EFFECT_H

#include "LedStrip.h"
#include "ColorUtils.h"

namespace LampEffectId {
  const uint8_t static_color = 0;
  const uint8_t beacon_light = 1;
  const uint8_t color_loop = 2;
  const uint8_t rotating_lines = 3;
  const uint8_t rotating_rainbow = 4;
};

namespace LampEffectEepromDataSize {
  const uint8_t static_color = RGB_COLOR_BYTES_LENGTH;
  const uint8_t beacon_light = 0;
  const uint8_t color_loop = 0;
  const uint8_t rotating_lines = 0;
  const uint8_t rotating_rainbow = 0;
};

class LampEffect {
  public:
    LedStrip* strip;
    uint8_t id;
    uint8_t eepromDataSize;

    LampEffect(LedStrip* the_strip, int id, int eepromDataSize)
      : strip(the_strip), id(id), eepromDataSize(eepromDataSize) {
    }
    static LampEffect* createEffect(LedStrip* strip, uint8_t effectId, uint8_t* bytes, uint8_t dataSize);
    static uint8_t dataSizeForEffectId(uint8_t effectId);

    virtual void setup() = 0;
    virtual void next() = 0;
    virtual uint8_t* toBytes() = 0;
    virtual ~LampEffect() {}
};

#endif
