#include "ColorLoop.h"
#include "LedStrip.h"


ColorLoop::ColorLoop(LedStrip* strip) : LampEffect(strip) {

}

void ColorLoop::setup() {
  this->currentColor.H = 0;
  this->currentColor.S = 1;
  this->currentColor.L = 0.5;
}

void ColorLoop::next() {
  this->currentColor.H += color_step;
  if (this->currentColor.H > 1) {
    this->currentColor.H -= 1;
  }
  strip->ClearTo(this->currentColor);
  strip->Show();
  delay(50);
}
