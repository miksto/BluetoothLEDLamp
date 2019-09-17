#ifndef ROTATING_LINES_H
#define ROTATING_LINES_H

#include "LampEffect.h"
#include "LedStrip.h"

class RotatingLines: public LampEffect {
  public:
    RotatingLines(LedStrip* strip);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
};

#endif
