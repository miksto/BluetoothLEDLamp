#include <NeoPixelBus.h>
#include "LampBLEServer.h"
#include "ColorUtils.h"
#include "Storage.h"
#include "Log.h"

#define LED_PIN 18
#define LED_COUNT 121

NeoPixelBus<NeoGrbwFeature, Neo800KbpsMethod> strip(LED_COUNT, LED_PIN);
LampBLEServer lampServer;

HslColor* newColor = NULL;
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
  displayColor(color);
  lampServer.setup();
  lampServer.setCallbacks(new LampCallbacks());
  lampServer.setColorCharacteristic(color);
}

void loop() {
  delay(2000);
  if (newColor != NULL) {
    if ((millis() - lastDebounceTime) > debounceDelay) {
      Storage::saveColor(*newColor);
      newColor = NULL;
    }
  }
}
