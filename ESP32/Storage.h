#ifndef STORAGE_H
#define STORAGE_H

#include "LampEffect.h"
#include "LedStrip.h"
#include "StaticColor.h"

namespace Storage
{
void init();
void saveEffect(LampEffect* effect);
LampEffect* loadEffect(LedStrip* strip);

void saveStaticColorEffect(StaticColor* effect);
StaticColor* loadStaticColorEffect(LedStrip* strip);


};
#endif
