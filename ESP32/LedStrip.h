#ifndef LAMP_CONSTANTS_H
#define LAMP_CONSTANTS_H
#include <NeoPixelBrightnessBus.h>

typedef NeoPixelBrightnessBus<NeoGrbwFeature, Neo800KbpsMethod> LedStrip;

namespace LedStripConstants
{
const uint8_t led_io_pin = 14;
const uint8_t led_count = 116;
};

#endif
