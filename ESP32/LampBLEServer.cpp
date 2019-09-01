#include "LampBLEServer.h"
#include "ColorUtils.h"
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

class ColorCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;

  public:
    ColorCallback(LampBLEServerCallbacks* lampCallbacks) {
      this->lampCallbacks = lampCallbacks;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
      uint8_t* data = pCharacteristic->getData();
      HslColor color = ColorUtils::bytesToHslColor(data);
      lampCallbacks->onSetColor(color);
    }
};

void LampBLEServer::setup(LampBLEServerCallbacks* callbacks) {
  BLEDevice::init("LedLAMP");
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  BLECharacteristic *pCharacteristic = pService->createCharacteristic(
                                         CHARACTERISTIC_UUID_COLOR,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  pCharacteristic->setCallbacks(new ColorCallback(callbacks));
  pService->start();

  BLEAdvertising *pAdvertising = pServer->getAdvertising();
  pAdvertising->start();
}
