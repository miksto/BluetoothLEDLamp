#ifndef COLOR_UTILS_H
#define COLOR_UTILS_H

#include <NeoPixelBus.h>
#define COLOR_BYTES_LENGTH 4
#define COLOR_HUE_INTEGER_MAX 65535
#define COLOR_LIGHTNESS_INTEGER_MAX 255
#define COLOR_SATURATION_INTEGER_MAX 255

namespace ColorUtils
{
RgbwColor hslToRgbw(HslColor hsl);
HslColor bytesToHslColor(uint8_t* bytes);
RgbwColor bytesToRgbwColor(uint8_t* bytes);

uint8_t* hslColorToBytes(HslColor color);
}
#endif
