#ifndef GLIMMERING_EFFECT_H
#define GLIMMERING_EFFECT_H

#include "LampEffect.h"
#include "LedStrip.h"
#include <NeoPixelBus.h>

class Pixel {
  public:
    float getAnimationValue();
    bool isCompleted();
    uint8_t pixelId;
    void reset();

  private:
    float getProgress();
    unsigned long startTime;
    int lifeTime;
};

class GlimmeringEffect: public LampEffect {
  private:
    static const uint8_t secondary_pixel_count = 20;
    RgbColor primaryColor;
    RgbColor secondaryColor;
    Pixel* secondaryPixels[secondary_pixel_count];

  public:
    GlimmeringEffect(LedStrip* strip, RgbColor primaryColor, RgbColor secondaryColor);
    ~GlimmeringEffect();
    virtual void setup();
    virtual void next();
    virtual uint8_t* toBytes();
    static GlimmeringEffect* fromBytes(LedStrip* strip, uint8_t* bytes);
};



#endif
