#ifndef PIXEL_CONTROL_EFFECT_H
#define PIXEL_CONTROL_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>


class PixelControlEffect: public LampEffect {
  private:
    RgbwColor* pixels;
    uint8_t pixelCount;

  public:
    PixelControlEffect(LedStrip* strip, RgbwColor* pixels,  uint8_t pixelCount);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static PixelControlEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};

#endif
