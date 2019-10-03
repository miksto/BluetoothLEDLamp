#ifndef SUNSET_EFFECT_H
#define SUNSET_EFFECT_H
#include "LedStrip.h"
#include "LampEffect.h"

class SunsetEffect : public LampEffect {
  private:
    float startHueValue;
    float endHueValue;
    float hueDistance;
    float visibleColorInterval = 0.1f;

    int timeDurationMinutes;
    unsigned long startTime;

    void applyGradient(float startHueValue);

  public:
    SunsetEffect(LedStrip* strip, int timeDurationMinutes, float visibleColorInterval, float startHueValue, float endHueValue);
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static SunsetEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};



#endif
