#include "LampBLEServer.h"
#include "ColorUtils.h"

class HslColorCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;

  public:
    HslColorCallback(LampBLEServerCallbacks* lampCallbacks) {
      this->lampCallbacks = lampCallbacks;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
        uint8_t* data = pCharacteristic->getData();
        HslColor color = ColorUtils::bytesToHslColor(data);
        lampCallbacks->onSetHslColor(color);
    }
};

class RgbColorCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;

  public:
    RgbColorCallback(LampBLEServerCallbacks* lampCallbacks) {
      this->lampCallbacks = lampCallbacks;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
        uint8_t* data = pCharacteristic->getData();
        RgbColor color = ColorUtils::bytesToRgbColor(data);
        lampCallbacks->onSetRgbColor(color);
    }
};

class NotificationAlertCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;

  public:
    NotificationAlertCallback(LampBLEServerCallbacks* lampCallbacks) {
      this->lampCallbacks = lampCallbacks;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
        lampCallbacks->onNotificationAlert();
    }
};

void LampBLEServer::setup() {
  BLEDevice::init("LedLAMP");
  this->server = BLEDevice::createServer();
  this->service = this->server->createService(LampBLEUUID::service);

  
  this->notificationCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_notification_alert,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  this->colorCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_color,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );


  this->service->start();
  BLEAdvertising *advertising = this->server->getAdvertising();
  advertising->start();
}

void LampBLEServer::setColorCharacteristicValue(RgbColor color) {
  uint8_t* data = ColorUtils::rgbColorToBytes(color);
  colorCharacteristic->setValue(data, RGB_COLOR_BYTES_LENGTH);
}

void LampBLEServer::setCallbacks(LampBLEServerCallbacks* callbacks) {
  this->colorCharacteristic->setCallbacks(new RgbColorCallback(callbacks));
  this->notificationCharacteristic->setCallbacks(new NotificationAlertCallback(callbacks));
}
