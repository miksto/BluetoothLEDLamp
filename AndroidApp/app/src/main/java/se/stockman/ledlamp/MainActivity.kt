package se.stockman.ledlamp

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


const val LAMP_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
const val LAMP_COLOR_CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
const val BLE_DEVICE_MAC = "30:AE:A4:CA:DE:76"
const val TAG = "ScanDeviceActivity"

class MainActivity : AppCompatActivity() {


    private var gatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery: " + gatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(TAG, "Disconnected from GATT server.")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> readCurrentColor()
                else -> Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (characteristic?.value != null) {
                val data = characteristic.value
                val brightness = data[0].toUByte().toInt()
                val saturation = data[1].toUByte().toInt()
                val hue = (data[3].toUByte().toInt() shl 8) or data[2].toUByte().toInt()

                hue_seek_bar.progress = hue
                saturation_seek_bar.progress = saturation
                brightness_seek_bar.progress = brightness
            }
        }

    }

    private fun readCurrentColor() {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))

        gatt?.readCharacteristic(characteristic)

    }

    private fun setColor(
        gatt: BluetoothGatt?,
        color: Int,
        saturation: Int,
        brightness: Int
    ) {
        val service = gatt?.getService(UUID.fromString(LAMP_SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(LAMP_COLOR_CHARACTERISTIC_UUID))


        val data = (((color shl 8) or saturation) shl 8) or brightness

        characteristic?.setValue(data, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
        gatt?.writeCharacteristic(characteristic)

    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
            bluetoothLeScanner.stopScan(this)
            gatt = result?.device?.connectGatt(this@MainActivity, false, gattCallback)
        }
    }

    private val lampScanFilter =
        ScanFilter.Builder().setDeviceAddress(BLE_DEVICE_MAC).build()

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager =
                applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            return bluetoothAdapter.bluetoothLeScanner
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seekbarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                setColor(
                    gatt,
                    hue_seek_bar.progress,
                    saturation_seek_bar.progress,
                    brightness_seek_bar.progress
                );
            }
        }

        hue_seek_bar.setOnSeekBarChangeListener(seekbarListener)
        saturation_seek_bar.setOnSeekBarChangeListener(seekbarListener)
        brightness_seek_bar.setOnSeekBarChangeListener(seekbarListener)
    }

    override fun onStart() {
        super.onStart()
        when (PermissionChecker.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )) {
            PermissionChecker.PERMISSION_GRANTED -> connectToLampIfNecessary()
            else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    private fun connectToLampIfNecessary() {
        if (gatt == null) {
            Log.i(TAG, "Start Scan")
            bluetoothLeScanner.startScan(
                mutableListOf<ScanFilter>(lampScanFilter),
                ScanSettings.Builder().build(),
                scanCallback
            )
        } else {
            gatt?.connect()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> when (grantResults) {
                intArrayOf(PackageManager.PERMISSION_GRANTED) -> {
                    Log.d("ScanDeviceActivity", "onRequestPermissionsResult(PERMISSION_GRANTED)")
                    bluetoothLeScanner.startScan(scanCallback)
                }
                else -> {
                    Log.d(
                        "ScanDeviceActivity",
                        "onRequestPermissionsResult(not PERMISSION_GRANTED)"
                    )
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onStop() {
        Log.d("ScanDeviceActivity", "onStop()")
        super.onStop()
        bluetoothLeScanner.stopScan(scanCallback)
        gatt?.disconnect();
    }

    override fun onDestroy() {
        super.onDestroy()
        gatt?.close()
        gatt = null
    }

}
