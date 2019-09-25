package se.stockman.ledlamp

import android.bluetooth.*
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.palette.graphics.Palette
import se.stockman.ledlamp.data.*
import java.util.*

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
const val LAMP_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
const val LAMP_COLOR_CHARACTERISTIC_UUID = "beb3283e-36e1-4688-b7f5-ea07361b26a8"
const val LAMP_EFFECT_CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
const val LAMP_NOTIFICATION_CHARACTERISTIC_UUID = "32e2d7c0-1c54-419f-945b-587ffef47e9c"
const val LAMP_DEBUG_CHARACTERISTIC_UUID = "32e2d7c0-1c54-419f-945b-777ffef47e9c"

class LedLamp(private val context: Context, private val callback: LampCallback) {
    companion object {
        val TAG: String? = LedLamp::class.simpleName
        val handler = Handler()
        const val SPOTIFY = "spotify"
    }

    interface LampCallback {
        fun onColorChanged(color: RgbColor)
        fun onConnectionStateChange(connected: Boolean)
    }

    private var device: BluetoothDevice? = null
    private var gatt: BluetoothGatt? = null
    private var connected = false

    fun connectToDevice(device: BluetoothDevice, context: Context) {
        this.device = device
        gatt = device.connectGatt(context, true, gattCallback)
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            device.createBond()
        }
    }

    fun hasNoDevice(): Boolean {
        return device == null
    }

    fun hasDeviceWithActiveConnection(): Boolean {
        return device != null && connected
    }

    fun hasDeviceButDisconnected(): Boolean {
        return device != null && !connected
    }

    fun disconnect() {
        gatt?.disconnect()
    }

    fun resumeConnection() {
        gatt?.connect()
    }

    fun destroy() {
        gatt?.close()
        gatt = null
        device = null
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt?.discoverServices()
                    Log.i(TAG, "Connected to GATT server.")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(TAG, "Disconnected from GATT server.")
                    connected = false
                    callback.onConnectionStateChange(connected)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    connected = true
                    callback.onConnectionStateChange(connected);
                    readCurrentColor()
                }
                else -> {
                    Log.w(TAG, "onServicesDiscovered received: $status")
                }

            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            characteristic?.value?.let {
                val rgbColor = LampEffect.getColorForStaticColorEffect(it)
                Log.i(TAG, "On charac read")
                callback.onColorChanged(rgbColor)
            }
        }
    }

    private fun readCurrentColor() {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))

        gatt?.readCharacteristic(characteristic)
    }

    fun setColor(color: HlsColor) {
        color.lightness = Integer.min(color.lightness, 180)
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))

        val byteArray = HlsColorDataObject(color).toByteArray()

        characteristic?.value = byteArray
        gatt?.writeCharacteristic(characteristic)
    }

    fun setColor(color: RgbColor) {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))

        val effect = LampEffect.createStaticColorEffect(color)
        characteristic?.value = effect.toByteArray()
        gatt?.writeCharacteristic(characteristic)
    }

    fun setEffect(effect: LampEffect) {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_EFFECT_CHARACTERISTIC_UUID))

        characteristic?.value = effect.toByteArray()
        gatt?.writeCharacteristic(characteristic)
    }


    fun notificationAlert(sbn: StatusBarNotification) {
        if (sbn.packageName.contains(SPOTIFY)) {
            val drawable = sbn.notification.getLargeIcon().loadDrawable(context)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val palette = Palette.from(bitmap).generate()
            val rgb = palette.dominantSwatch?.rgb
            rgb?.let {
                val color = RgbColor(it.red, it.green, it.blue)
                setColor(color)
            }
        } else {
            val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
            val characteristic =
                service?.getCharacteristic(UUID.fromString(LAMP_NOTIFICATION_CHARACTERISTIC_UUID))

            val bytes = ByteArray(1)
            val r = (1..255).shuffled().first()
            bytes[0] = (r and 0xFF).toByte()
            characteristic?.value = bytes
            handler.post {
                gatt?.writeCharacteristic(characteristic)
            }
        }
    }

    fun callDebugFunction() {

        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_DEBUG_CHARACTERISTIC_UUID))

        characteristic?.setValue("1")
        gatt?.writeCharacteristic(characteristic)
    }
}