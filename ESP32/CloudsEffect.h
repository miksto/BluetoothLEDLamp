#ifndef CLOUDS_EFFECT_H
#define CLOUDS_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>

class CloudsEffect: public LampEffect {
  private:
    RgbColor color1;
    RgbColor color2;

  public:
    CloudsEffect(LedStrip* strip, RgbColor color1, RgbColor color2);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static CloudsEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};



#endif
