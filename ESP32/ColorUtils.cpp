#include "ColorUtils.h"

namespace ColorUtils
{
RgbwColor hslToRgbw(HslColor hsl) {
  RgbwColor rgbw(hsl);
  uint8_t smallestValue = 255;
  smallestValue = min(smallestValue, rgbw.R);
  smallestValue = min(smallestValue, rgbw.G);
  smallestValue = min(smallestValue, rgbw.B);
  rgbw.R -= smallestValue;
  rgbw.G -= smallestValue;
  rgbw.B -= smallestValue;
  rgbw.W = smallestValue;
  return rgbw;
}

HslColor bytesToHslColor(uint8_t* bytes) {
  int hue = (bytes[3] << 8) | bytes[2];
  float hslH = (float) hue / COLOR_HUE_INTEGER_MAX;
  float hslS =  (float) bytes[1] / COLOR_SATURATION_INTEGER_MAX;
  float hslL = (float) bytes[0] / COLOR_LIGHTNESS_INTEGER_MAX;

  HslColor hslColor(hslH, hslS, hslL);
  return hslColor;
}

RgbwColor bytesToRgbwColor(uint8_t* bytes) {
  HslColor hslColor = bytesToHslColor(bytes);
  RgbwColor rgbw = hslToRgbw(hslColor);
  return rgbw;
}

uint8_t* hslColorToBytes(HslColor color) {
  uint8_t *bytes = new uint8_t[COLOR_BYTES_LENGTH];
  bytes[0] = (uint8_t) (color.L * COLOR_LIGHTNESS_INTEGER_MAX);
  bytes[1] = (uint8_t) (color.S * COLOR_SATURATION_INTEGER_MAX);

  int hue = color.H * COLOR_HUE_INTEGER_MAX;
  bytes[2] = (uint8_t) (hue & 0xFF);
  bytes[3] = (uint8_t) ((hue >> 8) & 0xFF);

  return bytes;
}
};
