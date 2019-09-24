#include "ColorUtils.h"

namespace ColorUtils
{
RgbwColor rgbToRgbw(RgbColor rgbColor) {
  uint8_t smallestValue = 255;
  smallestValue = min(smallestValue, rgbColor.R);
  smallestValue = min(smallestValue, rgbColor.G);
  smallestValue = min(smallestValue, rgbColor.B);
  RgbwColor rgbwColor;
  rgbwColor.R = rgbColor.R - smallestValue;
  rgbwColor.G = rgbColor.G - smallestValue;
  rgbwColor.B = rgbColor.B - smallestValue;
  rgbwColor.W = smallestValue;
  return rgbwColor;
}

HslColor bytesToHslColor(uint8_t* bytes) {
  int hue = (bytes[3] << 8) | bytes[2];
  float hslH = (float) hue / COLOR_HUE_INTEGER_MAX;
  float hslS =  (float) bytes[1] / COLOR_SATURATION_INTEGER_MAX;
  float hslL = (float) bytes[0] / COLOR_LIGHTNESS_INTEGER_MAX;

  HslColor hslColor(hslH, hslS, hslL);
  return hslColor;
}

RgbColor bytesToRgbColor(uint8_t* bytes) {
  RgbColor rgbColor(bytes[0], bytes[1], bytes[2]);
  return rgbColor;
}

uint8_t* hslColorToBytes(HslColor color) {
  uint8_t *bytes = new uint8_t[HSL_COLOR_BYTES_LENGTH];
  bytes[0] = (uint8_t) (color.L * COLOR_LIGHTNESS_INTEGER_MAX);
  bytes[1] = (uint8_t) (color.S * COLOR_SATURATION_INTEGER_MAX);

  int hue = color.H * COLOR_HUE_INTEGER_MAX;
  bytes[2] = (uint8_t) (hue & 0xFF);
  bytes[3] = (uint8_t) ((hue >> 8) & 0xFF);

  return bytes;
}

uint8_t* rgbColorToBytes(RgbColor color) {
  uint8_t *bytes = new uint8_t[RGB_COLOR_BYTES_LENGTH];
  bytes[0] = color.R;
  bytes[1] = color.G;
  bytes[2] = color.B;
  return bytes;
}
};
