package se.stockman.ledlamp

import android.bluetooth.*
import android.content.Context
import android.util.Log
import se.stockman.ledlamp.data.HlsColorDataObject
import java.util.*

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
const val LAMP_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
const val LAMP_COLOR_CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"

class LedLamp(private val callback: LampCallback) {

    interface LampCallback {
        fun onColorChanged(color: HlsColor)
        fun onConnectionStateChange(connected: Boolean)
    }

    private var device: BluetoothDevice? = null
    private var gatt: BluetoothGatt? = null
    private var connected = false

    fun connectToDevice(device: BluetoothDevice, context: Context) {
        this.device = device
        gatt = device.connectGatt(context, false, gattCallback)
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
                val hslColor = HlsColorDataObject.fromByteArray(it).color
                callback.onColorChanged(hslColor)
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
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))

        val byteArray = HlsColorDataObject(color).toByteArray()

        characteristic?.value = byteArray
        gatt?.writeCharacteristic(characteristic)
    }

}