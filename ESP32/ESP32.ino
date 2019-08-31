#include <NeoPixelBus.h>
#include "LampBLEServer.h"

#define LED_PIN 18
#define LED_COUNT 121

NeoPixelBus<NeoGrbwFeature, Neo800KbpsMethod> strip(LED_COUNT, LED_PIN);
LampBLEServer lampServer;

class LampCallbacks: public LampBLEServerCallbacks {
    void onSetColor(RgbwColor color) {
      strip.ClearTo(color);
      strip.Show();
    }
};

void setup() {
  Serial.begin(115200);
  strip.Begin();
  strip.Show();
  lampServer.setup(new LampCallbacks());
}

void loop() {
  delay(2000);
}
