#ifndef PATCHY_COLORS_EFFECT_H
#define PATCHY_COLORS_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"

class PatchyColorsEffect: public LampEffect {
  private:
    RgbColor color1;
    RgbColor color2;
    RgbColor color3;
  
  public:
    PatchyColorsEffect(LedStrip* strip,  RgbColor color1, RgbColor color2, RgbColor color3);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static PatchyColorsEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};

#endif
