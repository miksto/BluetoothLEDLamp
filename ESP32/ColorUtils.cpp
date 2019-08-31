#include <NeoPixelBus.h>

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

RgbwColor bytesToRgbwColor(std::string bytes) {
  int color = (bytes[3] << 8) | bytes[2];
  float hslH = (float)color / 65536;
  float hslS =  (float) bytes[1] / 255;
  float hslL = (float) bytes[0] / 255;

  HslColor hslColor(hslH, hslS, hslL);
  RgbwColor rgbw = hslToRgbw(hslColor);
  return rgbw;
}
};
