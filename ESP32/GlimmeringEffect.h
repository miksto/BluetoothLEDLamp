#ifndef GLIMMERING_EFFECT_H
#define GLIMMERING_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>


class GlimmeringEffect: public LampEffect {
  private:
    RgbColor primaryColor;
    RgbColor secondaryColor;

  public:
    GlimmeringEffect(LedStrip* strip, RgbColor primaryColor, RgbColor secondaryColor);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static GlimmeringEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};



#endif
