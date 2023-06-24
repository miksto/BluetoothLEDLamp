package se.stockman.ledlamp

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.service.notification.StatusBarNotification
import android.util.Log
import se.stockman.ledlamp.data.LampEffect
import se.stockman.ledlamp.data.RgbColor

@OptIn(ExperimentalUnsignedTypes::class)
class BluetoothLeService : Service() {

    private var pendingNotification: StatusBarNotification? = null
    private val listeners = mutableSetOf<LedLamp.LampCallback>()

    private val lampDeviceFoundCallback = object : LampDeviceDiscovery.Callback {
        override fun onDeviceFound(device: BluetoothDevice) {
            lampFinder.stop()
            if (ledLamp.hasNoDevice()) {
                ledLamp.connectToDevice(device, this@BluetoothLeService)
            }
        }
    }

    private val lampCallback = object : LedLamp.LampCallback {
        override fun onDimFactorReceived(dimFactor: Int) {
            listeners.forEach { it.onDimFactorReceived(dimFactor) }
        }

        override fun onConnectionStateChange(connected: Boolean) {
            listeners.forEach { it.onConnectionStateChange(connected) }
        }

        override fun onColorReceived(color: RgbColor) {
            listeners.forEach { it.onColorReceived(color) }
        }
    }

    private val ledLamp: LedLamp = LedLamp(this, lampCallback)
    private val lampFinder: LampDeviceDiscovery = LampDeviceDiscovery(lampDeviceFoundCallback)
    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }

    fun connectToLampIfNecessary() {
        if (ledLamp.hasNoDevice()) {
            Log.i(MainActivity.TAG, "Start Scan")
            lampFinder.findDevice()
        } else if (ledLamp.hasDeviceButDisconnected()) {
            ledLamp.resumeConnection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ledLamp.disconnect()
        ledLamp.destroy()
    }

    fun stopLampFinder() {
        lampFinder.stop()
    }

    fun setEffect(effect: LampEffect) {
        ledLamp.setEffect(effect)
    }

    fun setDimFactor(progress: Int) {
        ledLamp.setDimFactor(progress)
    }

    fun setColor(color: RgbColor) {
        ledLamp.setColor(color)
    }

    fun registerLampCallback(lampCallback: LedLamp.LampCallback) {
        listeners.add(lampCallback)
    }

    fun unregisterLampCallback(lampCallback: LedLamp.LampCallback) {
        listeners.remove(lampCallback)
    }

    fun handleNotification(notification: StatusBarNotification) {
        Log.i(NotificationListener.TAG, "Handling notification")
        if (ledLamp.hasDeviceWithActiveConnection()) {
            ledLamp.handleNotification(notification)
        } else {
            pendingNotification = notification
            connectToLampIfNecessary()
        }
    }
}