#include <NeoPixelBus.h>

namespace Storage
{
void init();
void saveColor(HslColor hsl);
HslColor loadColor();
};
