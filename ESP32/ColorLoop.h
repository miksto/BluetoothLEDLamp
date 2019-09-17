#ifndef COLOR_LOOP_H
#define COLOR_LOOP_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>


class ColorLoop: public LampEffect {
  private:
    HslColor currentColor;
    const float color_step = 0.005;
  public:
    ColorLoop(LedStrip* strip);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
};

#endif
