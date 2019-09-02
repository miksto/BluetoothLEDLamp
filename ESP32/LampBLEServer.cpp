#include "LampBLEServer.h"
#include "ColorUtils.h"

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

void LampBLEServer::setup() {
  BLEDevice::init("LedLAMP");
  server = BLEDevice::createServer();
  service = server->createService(SERVICE_UUID);

  colorCharacteristic = service->createCharacteristic(
                                         CHARACTERISTIC_UUID_COLOR,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  service->start();
  BLEAdvertising *advertising = server->getAdvertising();
  advertising->start();
}

void LampBLEServer::setColorCharacteristic(HslColor color) {
  uint8_t* data = ColorUtils::hslColorToBytes(color);
  colorCharacteristic->setValue(data, COLOR_BYTES_LENGTH);
}

void LampBLEServer::setCallbacks(LampBLEServerCallbacks* callbacks) {
  colorCharacteristic->setCallbacks(new ColorCallback(callbacks));
}
