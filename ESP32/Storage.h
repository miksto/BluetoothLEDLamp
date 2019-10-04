#ifndef STORAGE_H
#define STORAGE_H

#include "LampEffect.h"
#include "LedStrip.h"
#include "StaticColor.h"

namespace Storage
{
void init();
void saveEffect(LampEffect* effect, uint8_t start_index);
LampEffect* loadEffect(LedStrip* strip, uint8_t start_index);

void saveStaticColorEffect(StaticColor* effect);
StaticColor* loadStaticColorEffect(LedStrip* strip);

void saveEffect(LampEffect* effect);
LampEffect* loadEffect(LedStrip* strip);

uint8_t loadDimFactor();
void saveDimFactor(uint8_t dimFactor);

};
#endif
