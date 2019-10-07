#ifndef GRADIENT_EFFECT_H
#define GRADIENT_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>

class GradientEffect: public LampEffect {
  private:
    RgbColor color1;
    RgbColor color2;

  public:
    GradientEffect(LedStrip* strip, RgbColor color1, RgbColor color2);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static GradientEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};



#endif
