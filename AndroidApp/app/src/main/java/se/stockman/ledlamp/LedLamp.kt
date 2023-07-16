package se.stockman.ledlamp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.palette.graphics.Palette
import se.stockman.ledlamp.data.HlsColor
import se.stockman.ledlamp.data.HlsColorDataObject
import se.stockman.ledlamp.data.LampEffect
import se.stockman.ledlamp.data.RgbColor
import se.stockman.ledlamp.settings.Settings
import java.util.UUID

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
const val LAMP_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
const val LAMP_COLOR_CHARACTERISTIC_UUID = "beb3283e-36e1-4688-b7f5-ea07361b26a8"
const val LAMP_EFFECT_CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
const val LAMP_NOTIFICATION_CHARACTERISTIC_UUID = "32e2d7c0-1c54-419f-945b-587ffef47e9c"
const val LAMP_DEBUG_CHARACTERISTIC_UUID = "32e2d7c0-1c54-419f-945b-777ffef47e9c"
const val LAMP_DIM_FACTOR_CHARACTERISTIC_UUID = "836457c0-1c54-419f-945b-587ffef47e9c"

@ExperimentalUnsignedTypes
@SuppressLint("MissingPermission")
class LedLamp(private val context: Context, private val callback: LampCallback) {
    companion object {
        val TAG: String? = LedLamp::class.simpleName
        const val SPOTIFY = "spotify"
    }

    interface LampCallback {
        fun onColorReceived(color: RgbColor)
        fun onDimFactorReceived(dimFactor: Int)
        fun onConnectionStateChange(connected: Boolean)
    }

    private var gatt: BluetoothGatt? = null
    private var connected = false

    fun connectToDevice(device: BluetoothDevice, context: Context) {
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    fun hasGatt(): Boolean {
        return gatt == null
    }

    fun hasGattAndIsConnected(): Boolean {
        return gatt != null && connected
    }

    fun hasGattButIsDisconnected(): Boolean {
        return gatt != null && !connected
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
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt?.requestMtu(517)
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    connected = false
                    callback.onConnectionStateChange(connected)
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            gatt?.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    connected = true
                    callback.onConnectionStateChange(connected)
                    readCurrentState()
                }

                else -> {
                    Log.w(TAG, "onServicesDiscovered received: $status")
                }

            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (characteristic.uuid == UUID.fromString(LAMP_DIM_FACTOR_CHARACTERISTIC_UUID)) {
                callback.onDimFactorReceived(value[0].toUByte().toInt())
            } else if (characteristic.uuid == UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID)) {
                val rgbColor = LampEffect.getColorForStaticColorEffect(value)
                callback.onColorReceived(rgbColor)
                readDimFactor()
            }
        }
    }

    fun readCurrentState() {
        readCurrentColor()
    }

    private fun readCurrentColor() {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))
        gatt?.readCharacteristic(characteristic)
    }

    private fun readDimFactor() {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_DIM_FACTOR_CHARACTERISTIC_UUID))

        gatt?.readCharacteristic(characteristic)
    }

    fun setColor(color: HlsColor) {
        val colorDataObject = HlsColorDataObject(
            color.copy(lightness = color.lightness.coerceAtMost(180))
        )
        val characteristic =
            gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
                ?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))
                ?: return

        gatt?.writeCharacteristic(
            characteristic,
            colorDataObject.toByteArray(),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )
    }

    fun setColor(color: RgbColor) {
        val effect = LampEffect.createStaticColorEffect(color)
        val characteristic =
            gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
                ?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))
                ?: return

        gatt?.writeCharacteristic(
            characteristic,
            effect.toByteArray(),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )
    }

    fun setEffect(effect: LampEffect) {
        val characteristic =
            gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
                ?.getCharacteristic(UUID.fromString(LAMP_EFFECT_CHARACTERISTIC_UUID))
                ?: return

        gatt?.writeCharacteristic(
            characteristic,
            effect.toByteArray(),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )
    }


    fun handleNotification(sbn: StatusBarNotification) {
        if (Settings.isSpotifyIntegrationEnabled(context) && sbn.packageName.contains(SPOTIFY)) {
            val drawable = sbn.notification.getLargeIcon().loadDrawable(context)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val palette = Palette.from(bitmap).generate()

            palette.dominantSwatch
                ?.rgb?.takeUnless { it.red == it.green && it.red == it.blue }
                ?.let { setColor(RgbColor(it.red, it.green, it.blue)) }

        } else if (Settings.isNotificationFlashEnabled(context)) {
            val characteristic =
                gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
                    ?.getCharacteristic(UUID.fromString(LAMP_NOTIFICATION_CHARACTERISTIC_UUID))
                    ?: return

            val bytes = ByteArray(1)
            val r = (1..255).shuffled().first()
            bytes[0] = (r and 0xFF).toByte()

            gatt?.writeCharacteristic(
                characteristic,
                bytes,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        }
    }

    fun callDebugFunction() {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_DEBUG_CHARACTERISTIC_UUID)) ?: return
        gatt?.writeCharacteristic(
            characteristic,
            "1".toByteArray(),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )
    }

    fun setDimFactor(dimFactor: Int) {
        val characteristic =
            gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
                ?.getCharacteristic(UUID.fromString(LAMP_DIM_FACTOR_CHARACTERISTIC_UUID))
                ?: return

        gatt?.writeCharacteristic(
            characteristic,
            byteArrayOf(dimFactor.toByte()),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )
    }
}