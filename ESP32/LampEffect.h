#ifndef LAMP_EFFECT_H
#define LAMP_EFFECT_H

#include "LedStrip.h"

class LampEffect {
  public:
    LedStrip* strip;

    LampEffect(LedStrip* the_strip)
      : strip(the_strip){
    }

    virtual void setup() = 0;
    virtual void next() = 0;
};

#endif
