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
import se.stockman.ledlamp.data.HlsColor
import se.stockman.ledlamp.data.HlsColorDataObject
import se.stockman.ledlamp.data.LampEffect
import se.stockman.ledlamp.data.RgbColor
import se.stockman.ledlamp.settings.Settings
import java.util.*

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
class LedLamp(private val context: Context, private val callback: LampCallback) {
    companion object {
        val TAG: String? = LedLamp::class.simpleName
        val handler = Handler()
        const val SPOTIFY = "spotify"
    }

    interface LampCallback {
        fun onColorReceived(color: RgbColor)
        fun onDimFactorReceived(dimFactor: Int)
        fun onConnectionStateChange(connected: Boolean)
    }

    private var device: BluetoothDevice? = null
    private var gatt: BluetoothGatt? = null
    private var connected = false

    fun connectToDevice(device: BluetoothDevice, context: Context) {
        this.device = device
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
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
                    gatt?.requestMtu(517)
                    Log.i(TAG, "Connected to GATT server.")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(TAG, "Disconnected from GATT server.")
                    connected = false
                    callback.onConnectionStateChange(connected)
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.i(TAG, "onMtuChanged success: " + (status == BluetoothGatt.GATT_SUCCESS))
            gatt?.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    connected = true
                    callback.onConnectionStateChange(connected)
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
            if (characteristic?.uuid == UUID.fromString(LAMP_DIM_FACTOR_CHARACTERISTIC_UUID)) {
                characteristic?.value?.let {
                    callback.onDimFactorReceived(it[0].toUByte().toInt())
                }
            } else if (characteristic?.uuid == UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID)) {
                characteristic?.value?.let {
                    val rgbColor = LampEffect.getColorForStaticColorEffect(it)
                    callback.onColorReceived(rgbColor)
                    readDimFactor()
                }
            }
        }
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
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        gatt?.writeCharacteristic(characteristic)
    }


    fun notificationAlert(sbn: StatusBarNotification) {
        if (Settings.isSpotifyIntegrationEnabled(context) && sbn.packageName.contains(SPOTIFY)) {
            val drawable = sbn.notification.getLargeIcon().loadDrawable(context)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val palette = Palette.from(bitmap).generate()
            val rgb = palette.dominantSwatch?.rgb
            rgb?.let {
                val color = RgbColor(it.red, it.green, it.blue)
                setColor(color)
            }
        } else if (Settings.isNotificationFlashEnabled(context)) {
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

    fun setDimFactor(dimFactor: Int) {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_DIM_FACTOR_CHARACTERISTIC_UUID))

        characteristic?.value = byteArrayOf(dimFactor.toByte())
        gatt?.writeCharacteristic(characteristic)
    }
}