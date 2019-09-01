#ifndef LAMP_BLE_SERVER_H
#define LAMP_BLE_SERVER_H

#include <NeoPixelBus.h>
#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID_COLOR "beb5483e-36e1-4688-b7f5-ea07361b26a8"

class LampBLEServerCallbacks {
  public:
    virtual void onSetColor(HslColor color);
};

class LampBLEServer {
  public:
    void setup(LampBLEServerCallbacks* callbacks);
};
#endif
