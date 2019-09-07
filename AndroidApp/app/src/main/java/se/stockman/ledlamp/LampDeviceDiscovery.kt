package se.stockman.ledlamp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.util.Log

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
const val BLE_DEVICE_MAC = "30:AE:A4:CA:DE:76"

class LampDeviceDiscovery(callback: Callback) {
    interface Callback {
        fun onDeviceFound(device: BluetoothDevice);
    }

    private val lampScanFilter =
        ScanFilter.Builder().setDeviceAddress(BLE_DEVICE_MAC).build()

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter.bluetoothLeScanner
        }

    fun findDevice() {
        bluetoothLeScanner.startScan(
            mutableListOf<ScanFilter>(lampScanFilter),
            ScanSettings.Builder().build(),
            scanCallback
        )
    }

    fun stop() {
        bluetoothLeScanner.stopScan(scanCallback);
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
            bluetoothLeScanner.stopScan(this)
            result?.device?.let {
                callback.onDeviceFound(it)
            }
        }
    }
}