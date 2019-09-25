#ifndef STATIC_COLOR_H
#define STATIC_COLOR_H

#include <NeoPixelBus.h>
#include "LampEffect.h"
#include "LedStrip.h"

class StaticColor: public LampEffect {
  public:
    RgbColor color;
    StaticColor(LedStrip* strip, RgbColor color);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static StaticColor* fromBytes(LedStrip* strip, uint8_t* bytes);
};
#endif
