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
  const uint8_t glimmering_effect = 5;
  const uint8_t sunset_effect = 6;
  const uint8_t stroboscope_effect = 7;
  const uint8_t gradient_effect = 8;
  const uint8_t patchy_colors_effect = 9;
  const uint8_t clouds_effect = 10;
  const uint8_t pixel_control_effect = 11;
};

namespace LampEffectEepromDataSize {
  const uint8_t static_color = RGB_COLOR_BYTES_LENGTH;
  const uint8_t beacon_light = 0;
  const uint8_t color_loop = 0;
  const uint8_t rotating_lines = 0;
  const uint8_t rotating_rainbow = 2;
  const uint8_t glimmering_effect = RGB_COLOR_BYTES_LENGTH * 2;
  const uint8_t sunset_effect = 4;
  const uint8_t stroboscope_effect = 0;
  const uint8_t gradient_effect = RGB_COLOR_BYTES_LENGTH * 2;
  const uint8_t patchy_colors_effect = RGB_COLOR_BYTES_LENGTH * 3;
  const uint8_t clouds_effect = RGB_COLOR_BYTES_LENGTH * 2;
  const uint8_t pixel_control_effect = RGB_COLOR_BYTES_LENGTH * LedStripConstants::led_count;
};

class LampEffect {
  public:
    LedStrip* strip;
    uint8_t id;
    uint8_t eepromDataSize;

    LampEffect(LedStrip* the_strip, int id, int eepromDataSize)
      : strip(the_strip), id(id), eepromDataSize(eepromDataSize) {
    }
    static LampEffect* createEffect(LedStrip* strip, uint8_t* bytes);
    
    static uint8_t dataSizeForEffectId(uint8_t effectId);

    virtual void setup() = 0;
    virtual void next() = 0;
    virtual uint8_t* toBytes() = 0;
    virtual ~LampEffect() {}
};

#endif
