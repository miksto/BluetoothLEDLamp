#ifndef LAMP_BLE_SERVER_H
#define LAMP_BLE_SERVER_H

#include <NeoPixelBus.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLEUUID.h>
#include "LampEffect.h"
#include "StaticColor.h"

namespace LampBLEUUID
{
const BLEUUID service("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
const BLEUUID characteristic_effect("beb5483e-36e1-4688-b7f5-ea07361b26a8");
const BLEUUID characteristic_static_color("beb3283e-36e1-4688-b7f5-ea07361b26a8");
const BLEUUID characteristic_notification_alert("32e2d7c0-1c54-419f-945b-587ffef47e9c");
const BLEUUID characteristic_dim_factor("836457c0-1c54-419f-945b-587ffef47e9c");
const BLEUUID characteristic_debug("32e2d7c0-1c54-419f-945b-777ffef47e9c");
}

class LampBLEServerCallbacks {
  public:
    virtual void onSetHslColor(HslColor color);
    virtual void onSetEffect(LampEffect* effect);
    virtual void onSetStaticColor(StaticColor* effect);
    virtual void onSetDimFactor(uint8_t dimFactor);
    virtual void onNotificationAlert();
    virtual void debugButtonPress();
};

class LampBLEServer {
  private:
    BLEServer* server;
    BLEService* service;
    BLECharacteristic* effectCharacteristic;
    BLECharacteristic* staticColorCharacteristic;
    BLECharacteristic* notificationCharacteristic;
    BLECharacteristic* dimFactorCharacteristic;
    BLECharacteristic* debugButtonCharacteristic;

  public:
    void setup();
    void setCallbacks(LampBLEServerCallbacks* callbacks, LedStrip* strip);
    void setColorCharacteristicValue(RgbColor color);
    void setDimFactorCharacteristicValue(uint8_t dimFactor);
};
#endif
