#include "LampBLEServer.h"
#include <NeoPixelBus.h>
#include "ColorUtils.h"
#include "Storage.h"
#include "Log.h"
#include "LedStrip.h"
#include "RotatingLines.h"
#include "RotatingRainbow.h"
#include "ColorLoop.h"
#include "LampEffect.h"

LedStrip strip(LedStripConstants::led_count, LedStripConstants::led_io_pin);
LampBLEServer lampServer;
LampEffect* effect = new ColorLoop(&strip);

enum class Mode {effect, static_color};
Mode current_mode = Mode::effect;

HslColor* newColor = nullptr;
unsigned long lastDebounceTime = 0;
unsigned long debounceDelay = 1000;

void displayColor(HslColor color) {
  RgbwColor rgbwColor = ColorUtils::hslToRgbw(color);
  strip.ClearTo(rgbwColor);
  strip.Show();
}

void flashOn(RgbwColor color) {
  RgbwColor black(0, 0, 0, 0);
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

class LampCallbacks: public LampBLEServerCallbacks {
    void onSetColor(HslColor color) {
      current_mode = Mode::static_color;
      displayColor(color);
      newColor = new HslColor(color.H, color.S, color.L);
      lastDebounceTime = millis();
    }
};

void setup() {
  Serial.begin(115200);
  Storage::init();
  strip.Begin();
  HslColor color = Storage::loadColor();
  flashOn(color);
  Log::logColor("StoredColor", color);
  displayColor(color);
  lampServer.setup();
  lampServer.setCallbacks(new LampCallbacks());
  lampServer.setColorCharacteristic(color);
  effect->setup();
}

void loop() {
  if (current_mode == Mode::effect) {
    effect->next();
  }
  if (newColor != NULL) {
    if ((millis() - lastDebounceTime) > debounceDelay) {
      Storage::saveColor(*newColor);
      newColor = NULL;
    }
  }
}
