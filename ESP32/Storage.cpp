#include "Storage.h"
#include "ColorUtils.h"

#include <EEPROM.h>

#define ADDR_COLOR 0
#define EEPROM_SIZE 4


namespace Storage
{

void init() {
  EEPROM.begin(EEPROM_SIZE);
}

void saveColor(HslColor hsl) {
  uint8_t* bytes = ColorUtils::hslColorToBytes(hsl);
  for (int i = 0; i < COLOR_BYTES_LENGTH; i++) {
    EEPROM.write(ADDR_COLOR + i, bytes[i]);
  }
  EEPROM.commit();
}

HslColor loadColor() {
  uint8_t* bytes = new uint8_t[COLOR_BYTES_LENGTH];

  for (int i = 0; i < COLOR_BYTES_LENGTH; i++) {
    bytes[i] = EEPROM.read(ADDR_COLOR + i);
  }
  HslColor color = ColorUtils::bytesToHslColor(bytes);
  return color;
}
};
