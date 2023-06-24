package se.stockman.ledlamp

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import se.stockman.ledlamp.data.RgbColor

/**
 * Created by Mikael Stockman on 2019-09-07.
 */

@ExperimentalUnsignedTypes
class NotificationListener : NotificationListenerService() {

    companion object {
        val TAG: String? = NotificationListener::class.simpleName

        fun createIntent(context: Context): Intent {
            return Intent(context, NotificationListener::class.java)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private var pendingNotification: StatusBarNotification? = null
    private val lampDeviceFoundCallback = object : LampDeviceDiscovery.Callback {
        override fun onDeviceFound(device: BluetoothDevice) {
            lampFinder.stop()
            if (ledLamp.hasNoDevice()) {
                ledLamp.connectToDevice(device, this@NotificationListener)
            }
        }
    }

    private val lampCallback = object : LedLamp.LampCallback {
        override fun onDimFactorReceived(dimFactor: Int) {
            //Do nothing
        }

        override fun onConnectionStateChange(connected: Boolean) {
            if (connected) {
                if (pendingNotification != null) {
                    pendingNotification?.let {
                        ledLamp.notificationAlert(it)
                    }
                    pendingNotification = null
                    ledLamp.disconnect()
                }
            }
        }

        override fun onColorReceived(color: RgbColor) {
            //Do nothing
        }
    }

    private val lampFinder: LampDeviceDiscovery = LampDeviceDiscovery(lampDeviceFoundCallback)
    private val ledLamp: LedLamp = LedLamp(this, lampCallback)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        handleNotificationChange(sbn)
    }

    private fun handleNotificationChange(sbn: StatusBarNotification?) {
        sbn?.let {
            Log.i(TAG, "Handling notification")
            if (ledLamp.hasDeviceWithActiveConnection()) {
                ledLamp.notificationAlert(it)
                ledLamp.disconnect()
            } else {
                pendingNotification = it
                connectToLamp()
            }
        }
    }

    private fun connectToLamp() {
        if (ledLamp.hasNoDevice()) {
            lampFinder.findDevice()
        } else if (ledLamp.hasDeviceButDisconnected()) {
            ledLamp.resumeConnection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ledLamp.destroy()
        lampFinder.stop()
    }
}