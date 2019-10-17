#include "GlimmeringEffect.h"
#include "ColorUtils.h"
#include "Log.h"
#include "LedStrip.h"

void Pixel::reset() {
  this->pixelId = rand() % LedStripConstants::led_count;
  this->startTime = millis() + (rand() % 5000);
  this->lifeTime = 7000 + (rand() % 5000);
}

bool Pixel::isCompleted() {
  return this->getProgress() >= 1;
}

float Pixel::getProgress() {
  unsigned long now = millis();
  if (now < this->startTime) {
    return 0;
  } else {
    float timeDiff = now - this->startTime;
    float progress = timeDiff / this->lifeTime;
    progress = min(progress, 1.0f);
    return progress;
  }
}

float Pixel::getAnimationValue() {
  float progress = this->getProgress();

  if (progress < 0.3f) {
    return progress / 0.3f;
  } else if (progress < 0.7f) {
    return 1;
  } else {
    return 1 - ((progress - 0.7f) / 0.3f);
  }
}

GlimmeringEffect::GlimmeringEffect(LedStrip* strip, RgbColor primaryColor, RgbColor secondaryColor)
  : LampEffect(strip, LampEffectId::glimmering_effect, LampEffectEepromDataSize::glimmering_effect) {
  this->primaryColor = primaryColor;
  this->secondaryColor = secondaryColor;
}

GlimmeringEffect::~GlimmeringEffect() {
  for (int i = 0; i < GlimmeringEffect::secondary_pixel_count; i++) {
    delete this->secondaryPixels[i];
  }
}

void GlimmeringEffect::setup() {
  RgbwColor rgbwPrimaryColor = ColorUtils::rgbToRgbw(this->primaryColor);
  RgbwColor rgbwSecondaryColor = ColorUtils::rgbToRgbw(this->secondaryColor);

  for (int i = 0; i < GlimmeringEffect::secondary_pixel_count; i++) {
    this->secondaryPixels[i] = new Pixel();
    this->secondaryPixels[i]->reset();
  }

  this->strip->ClearTo(rgbwPrimaryColor);
  this->strip->Show();
}

void GlimmeringEffect::next() {
  RgbwColor rgbwPrimaryColor = ColorUtils::rgbToRgbw(this->primaryColor);
  RgbwColor rgbwSecondaryColor = ColorUtils::rgbToRgbw(this->secondaryColor);

  this->strip->ClearTo(rgbwPrimaryColor);

  for (int i = 0; i < GlimmeringEffect::secondary_pixel_count; i++) {
    Pixel* pixel = this->secondaryPixels[i];
    if (pixel->isCompleted()) {
      pixel->reset();
    }
    float animationValue = pixel->getAnimationValue();
    RgbwColor blendedColor = RgbwColor::LinearBlend(rgbwPrimaryColor, rgbwSecondaryColor, animationValue);

    this->strip->SetPixelColor(pixel->pixelId, blendedColor);
  }

  this->strip->Show();
  delay(50);
}

uint8_t* GlimmeringEffect::toBytes() {
  uint8_t* bytes = new uint8_t[this->eepromDataSize];
  uint8_t* color1bytes = ColorUtils::rgbColorToBytes(this->primaryColor);
  uint8_t* color2bytes = ColorUtils::rgbColorToBytes(this->secondaryColor);
  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[i] = color1bytes[i];
  }

  for (int i = 0; i < RGB_COLOR_BYTES_LENGTH; i++) {
    bytes[RGB_COLOR_BYTES_LENGTH + i] = color2bytes[i];
  }
  delete[] color1bytes;
  delete[] color2bytes;
  return bytes;
}

GlimmeringEffect* GlimmeringEffect::fromBytes(LedStrip* strip, uint8_t* bytes) {
  RgbColor primaryColor = ColorUtils::bytesToRgbColor(bytes);
  RgbColor secondaryColor = ColorUtils::bytesToRgbColor(&bytes[RGB_COLOR_BYTES_LENGTH]);
  GlimmeringEffect* effect = new GlimmeringEffect(strip, primaryColor, secondaryColor);
  return effect;
}
