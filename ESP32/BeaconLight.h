#ifndef BEACON_LIGHT_H
#define BEACON_LIGHT_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>


class BeaconLight: public LampEffect {
  private:
    void displayArray();
    const static int colorArrayLength = 6 * 22;
    RgbwColor* colorArray[colorArrayLength];
    int arrayPos = 0;

  public:
    BeaconLight(LedStrip* strip);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
};

#endif
