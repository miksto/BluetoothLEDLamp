#ifndef LOG_H
#define LOG_H

#include <stdint.h>
#include <string>
#include <NeoPixelBus.h>

namespace Log
{
void logColorBytes(const char* tag, uint8_t* bytes);
void logColorAsBytes(const char* tag, HslColor color);
};
#endif
