package se.stockman.ledlamp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import se.stockman.ledlamp.data.RgbColor

/**
 * Created by Mikael Stockman on 2019-09-07.
 */

@ExperimentalUnsignedTypes
class NotificationListener : NotificationListenerService() {

    private var bluetoothService: BluetoothLeService? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            bluetoothService?.registerLampCallback(lampCallback)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }

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

    override fun onCreate() {
        super.onCreate()
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private var pendingNotification: StatusBarNotification? = null

    private val lampCallback = object : LedLamp.LampCallback {
        override fun onDimFactorReceived(dimFactor: Int) {
            //Do nothing
        }

        override fun onConnectionStateChange(connected: Boolean) {
            if (connected) {
                if (pendingNotification != null) {
                    pendingNotification?.let {
                        bluetoothService?.handleNotification(it)
                    }
                    pendingNotification = null
                }
            }
        }

        override fun onColorReceived(color: RgbColor) {
            //Do nothing
        }
    }


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            handleNotificationChange(it)
        }
    }

    private fun handleNotificationChange(notification: StatusBarNotification) {
        bluetoothService?.handleNotification(notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService?.stopLampFinder()
        bluetoothService?.unregisterLampCallback(lampCallback)
        unbindService(serviceConnection)
    }
}