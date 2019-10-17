#include "LampBLEServer.h"
#include <NeoPixelBus.h>
#include "ColorUtils.h"
#include "Storage.h"
#include "Log.h"
#include "LedStrip.h"
#include "RotatingLines.h"
#include "RotatingRainbow.h"
#include "BeaconLight.h"
#include "StaticColor.h"

#include "ColorLoop.h"
#include "LampEffect.h"

LedStrip strip(LedStripConstants::led_count, LedStripConstants::led_io_pin);
LampBLEServer lampServer;

//Has to be pointer to support polymorphism
LampEffect* effect;
StaticColor* staticColorEffect;
uint8_t currentDimFactor;

boolean isDirty = false;
unsigned long lastDebounceTime = 0;
unsigned long debounceDelay = 1000;

bool pendingAlert = false;

void displayColor(RgbColor color) {
  Log::logColor("ESP32", color);
  delete effect;
  staticColorEffect = new StaticColor(&strip, color);
  effect = staticColorEffect;
  effect->setup();
}

void notificationAlert() {
  RgbwColor black(0, 0, 0, 0);
  RgbwColor color(0, 0, 0, 80);
  int blink_delay = 150;
  strip.ClearTo(black);
  strip.Show();
  delay(blink_delay);

  strip.ClearTo(color);
  strip.Show();
  delay(blink_delay);

  strip.ClearTo(black);
  strip.Show();
  delay(blink_delay);

  strip.ClearTo(color);
  strip.Show();
  delay(blink_delay);

  strip.ClearTo(black);
  strip.Show();
  delay(blink_delay);
}

void setDirty() {
  lastDebounceTime = millis();
  isDirty = true;
}

class LampCallbacks: public LampBLEServerCallbacks {
    void onSetHslColor(HslColor color) {
      RgbColor rgbColor(color);
      displayColor(rgbColor);
      setDirty();
    }

    void onSetEffect(LampEffect* newEffect) {
      if (effect->id != LampEffectId::static_color) {
        delete effect;
      }

      effect = newEffect;
      effect->setup();
      setDirty();
    }

    void onSetStaticColor(StaticColor* newEffect) {
      delete staticColorEffect;
      staticColorEffect = newEffect;
      effect = staticColorEffect;
      effect->setup();
      setDirty();
    }

    void onNotificationAlert() {
      pendingAlert = true;
    }

    void onSetDimFactor(uint8_t dimFactor) {
      strip.SetBrightness(dimFactor);
      strip.Show();
      currentDimFactor = dimFactor;
    }

    void debugButtonPress() {
      ColorLoop* rainbow = new ColorLoop(&strip);
      effect = rainbow;
      effect->setup();
      setDirty();
    }
};

void setup() {
  Serial.begin(115200);
  Storage::init();
  strip.Begin();
  
  lampServer.setup();
  lampServer.setCallbacks(new LampCallbacks(), &strip);

  staticColorEffect = Storage::loadStaticColorEffect(&strip);
  if (staticColorEffect == nullptr) {
    RgbColor color(100, 100, 100);
    staticColorEffect = new StaticColor(&strip, color);
  }

  effect = Storage::loadEffect(&strip);
  if (effect == nullptr) {
    effect = staticColorEffect;
  }
  
  effect->setup();
  
  currentDimFactor = Storage::loadDimFactor();
  strip.SetBrightness(currentDimFactor);
  
  lampServer.setColorCharacteristicValue(staticColorEffect->color);
  lampServer.setDimFactorCharacteristicValue(currentDimFactor);
}

void loop() {
  if (pendingAlert) {
    notificationAlert();
    effect->setup();
    pendingAlert = false;
  }
  
  effect->next();

  if (isDirty) {
    if ((millis() - lastDebounceTime) > debounceDelay) {
      Storage::saveStaticColorEffect(staticColorEffect);
      //Too much data in PixelControlEffect for EEPROM
      if (effect->id != LampEffectId::pixel_control_effect) {
        Storage::saveEffect(effect);
      }
      Storage::saveDimFactor(currentDimFactor);
      isDirty = false;
    }
  }
}
