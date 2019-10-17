#include "LampBLEServer.h"
#include "ColorUtils.h"
#include "LedStrip.h"
#include "Log.h"

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

class EffectCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;
    LedStrip* strip;

  public:
    EffectCallback(LampBLEServerCallbacks* lampCallbacks, LedStrip* strip) {
      this->lampCallbacks = lampCallbacks;
      this->strip = strip;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
        uint8_t* bytes = pCharacteristic->getData();
        LampEffect* effect = LampEffect::createEffect(this->strip, bytes);
        lampCallbacks->onSetEffect(effect);
    }
};

class StaticColorCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;
    LedStrip* strip;

  public:
    StaticColorCallback(LampBLEServerCallbacks* lampCallbacks, LedStrip* strip) {
      this->lampCallbacks = lampCallbacks;
      this->strip = strip;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
        Serial.println("StaticColorCallback");
        uint8_t* bytes = pCharacteristic->getData();
        StaticColor* effect = (StaticColor*) LampEffect::createEffect(this->strip, bytes);
        lampCallbacks->onSetStaticColor(effect);
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


class DebugButtonCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;

  public:
    DebugButtonCallback(LampBLEServerCallbacks* lampCallbacks) {
      this->lampCallbacks = lampCallbacks;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
        lampCallbacks->debugButtonPress();
    }
};

class DimFactorCallback: public BLECharacteristicCallbacks {
  private:
    LampBLEServerCallbacks* lampCallbacks;

  public:
    DimFactorCallback(LampBLEServerCallbacks* lampCallbacks) {
      this->lampCallbacks = lampCallbacks;
    }

    void onWrite(BLECharacteristic *pCharacteristic) {
      uint8_t* bytes = pCharacteristic->getData();
      lampCallbacks->onSetDimFactor(bytes[0]);
    }
};

void LampBLEServer::setup() {
  BLEDevice::init("LedLAMP");
  BLEDevice::setMTU(517);

  this->server = BLEDevice::createServer();
  this->service = this->server->createService(LampBLEUUID::service);

  
  this->notificationCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_notification_alert,
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  this->effectCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_effect,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  this->staticColorCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_static_color,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );
  this->dimFactorCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_dim_factor,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );


   this->debugButtonCharacteristic = this->service->createCharacteristic(
                                         LampBLEUUID::characteristic_debug,
                                         BLECharacteristic::PROPERTY_WRITE
                                       );


  this->service->start();
  BLEAdvertising *advertising = this->server->getAdvertising();
  advertising->start();
}

void LampBLEServer::setColorCharacteristicValue(RgbColor color) {
  uint8_t* data = ColorUtils::rgbColorToBytes(color);
  this->staticColorCharacteristic->setValue(data, RGB_COLOR_BYTES_LENGTH);
}

void LampBLEServer::setDimFactorCharacteristicValue(uint8_t dimFactor) {
  uint16_t tempDimFactor = dimFactor;
  this->dimFactorCharacteristic->setValue(tempDimFactor);
}

void LampBLEServer::setCallbacks(LampBLEServerCallbacks* callbacks, LedStrip* strip) {
  this->effectCharacteristic->setCallbacks(new EffectCallback(callbacks, strip));
  this->staticColorCharacteristic->setCallbacks(new StaticColorCallback(callbacks, strip));
  this->notificationCharacteristic->setCallbacks(new NotificationAlertCallback(callbacks));
  this->debugButtonCharacteristic->setCallbacks(new DebugButtonCallback(callbacks));
  this->dimFactorCharacteristic->setCallbacks(new DimFactorCallback(callbacks));
}
