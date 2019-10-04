#ifndef ROTATING_RAINBOW_H
#define ROTATING_RAINBOW_H

#include "LampEffect.h"
#include "LedStrip.h"

class RotatingRainbow: public LampEffect {
  private:
    float hueValue;
    float colorInterval;
    float speed;
    void applyGradient();
  
  public:
    RotatingRainbow(LedStrip* strip, float colorInterval, float speed);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static RotatingRainbow* fromBytes(LedStrip* strip, uint8_t* bytes);
};

#endif
