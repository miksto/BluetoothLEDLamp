package se.stockman.ledlamp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
const val BLE_DEVICE_MAC = "30:AE:A4:CA:DE:76"

@SuppressLint("MissingPermission")
class LampDeviceDiscovery(val callback: Callback) {
    companion object {
        val TAG: String? = LampDeviceDiscovery::class.simpleName
    }

    interface Callback {
        fun onDeviceFound(device: BluetoothDevice)
    }

    private val lampScanFilter =
        ScanFilter.Builder().setDeviceAddress(BLE_DEVICE_MAC).build()

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val bluetoothLeScanner by lazy { bluetoothAdapter?.bluetoothLeScanner }

    fun findDevice() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val device =
            pairedDevices?.find { bluetoothDevice -> bluetoothDevice.address == BLE_DEVICE_MAC }

        if (device != null) {
            Log.i(TAG, "Found bonded device")
            this.callback.onDeviceFound(device)
        } else {
            Log.i(TAG, "No bonded device, starting scanning")
            bluetoothLeScanner?.startScan(
                listOf(lampScanFilter),
                ScanSettings.Builder().build(),
                scanCallback
            )
        }
    }

    fun stop() {
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
            bluetoothLeScanner?.stopScan(this)
            result?.device?.let {
                callback.onDeviceFound(it)
            }
        }
    }
}