#ifndef LAMP_BLE_SERVER_H
#define LAMP_BLE_SERVER_H

#include <NeoPixelBus.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLEUUID.h>

namespace LampBLEUUID
{
const BLEUUID service("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
const BLEUUID characteristic_color("beb5483e-36e1-4688-b7f5-ea07361b26a8");
const BLEUUID characteristic_notification_alert("32e2d7c0-1c54-419f-945b-587ffef47e9c");
}

class LampBLEServerCallbacks {
  public:
    virtual void onSetHslColor(HslColor color);
    virtual void onSetRgbColor(RgbColor color);
    virtual void onNotificationAlert();
};

class LampBLEServer {
  private:
    BLEServer* server;
    BLEService* service;
    BLECharacteristic* colorCharacteristic;
    BLECharacteristic* notificationCharacteristic;

  public:
    void setup();
    void setCallbacks(LampBLEServerCallbacks* callbacks);
    void setColorCharacteristicValue(RgbColor color);
};
#endif
