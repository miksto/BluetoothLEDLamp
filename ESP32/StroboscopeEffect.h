#ifndef STROBOSCOPE_EFFECT_H
#define STROBOSCOPE_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"

class StroboscopeEffect: public LampEffect {
  public:
    StroboscopeEffect(LedStrip* strip);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
};

#endif
