#include "Arduino.h"
#include "Log.h"
#include "ColorUtils.h"

namespace Log
{
void logColorBytes(const char* tag, uint8_t* bytes) {
  char buffer [50];
  sprintf (buffer, "%s: %02X %02X %02X %02X", tag, bytes[0], bytes[1], bytes[2], bytes[3]);
  Serial.println(buffer);
}

void logColorAsBytes(const char* tag, HslColor color) {
  uint8_t* bytes = ColorUtils::hslColorToBytes(color);
  Log::logColorBytes(tag, bytes);
}
};
