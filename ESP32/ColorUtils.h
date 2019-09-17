#ifndef COLOR_UTILS_H
#define COLOR_UTILS_H

#include <NeoPixelBus.h>
#define HSL_COLOR_BYTES_LENGTH 4
#define RGB_COLOR_BYTES_LENGTH 3
#define COLOR_HUE_INTEGER_MAX 65535
#define COLOR_LIGHTNESS_INTEGER_MAX 255
#define COLOR_SATURATION_INTEGER_MAX 255


//Nice Orange: H:1499 S:255 L:125


namespace ColorUtils
{
RgbwColor rgbToRgbw(RgbColor hsl);

HslColor bytesToHslColor(uint8_t* bytes);
RgbColor bytesToRgbColor(uint8_t* bytes);

uint8_t* rgbColorToBytes(RgbColor color);
uint8_t* hslColorToBytes(HslColor color);
}
#endif
