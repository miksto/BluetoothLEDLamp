#ifndef LAMP_CONSTANTS_H
#define LAMP_CONSTANTS_H
#include <NeoPixelBus.h>

typedef NeoPixelBus<NeoGrbwFeature, Neo800KbpsMethod> LedStrip;

namespace LedStripConstants
{
const int led_io_pin = 14;
const int led_count = 117;
};

#endif
