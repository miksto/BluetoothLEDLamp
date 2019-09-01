#ifndef STORAGE_H
#define STORAGE_H

#include <NeoPixelBus.h>

namespace Storage
{
void init();
void saveColor(HslColor hsl);
HslColor loadColor();
};
#endif
