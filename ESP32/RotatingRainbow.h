#ifndef ROTATING_RAINBOW_H
#define ROTATING_RAINBOW_H

#include "LampEffect.h"
#include "LedStrip.h"

class RotatingRainbow: public LampEffect {
  public:
    RotatingRainbow(LedStrip* strip);
    virtual void setup();
    virtual void next();
};

#endif
