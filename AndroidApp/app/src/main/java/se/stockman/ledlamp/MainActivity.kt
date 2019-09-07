package se.stockman.ledlamp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import kotlinx.android.synthetic.main.content_main.*


const val LAMP_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
const val LAMP_COLOR_CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
const val BLE_DEVICE_MAC = "30:AE:A4:CA:DE:76"
const val TAG = "ScanDeviceActivity"

class MainActivity : AppCompatActivity() {

    val handler = Handler()

    private val lampCallback = object : LedLamp.LampCallback {
        override fun onConnectionStateChange(connected: Boolean) {
            handler.post {
                hue_seek_bar.isEnabled = connected
                saturation_seek_bar.isEnabled = connected
                lightness_seek_bar.isEnabled = connected
            }
        }

        override fun onColorChanged(color: HlsColor) {
            hue_seek_bar.progress = color.hue
            saturation_seek_bar.progress = color.saturation
            lightness_seek_bar.progress = color.lightness
        }
    }

    private var ledLamp: LedLamp = LedLamp(lampCallback)

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
            bluetoothLeScanner.stopScan(this)
            if (ledLamp.hasNoDevice()) {
                result?.device?.let {
                    ledLamp.connectToDevice(it, this@MainActivity)
                }
            }
        }
    }

    private val lampScanFilter =
        ScanFilter.Builder().setDeviceAddress(BLE_DEVICE_MAC).build()

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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
                val color = HlsColor(
                    hue_seek_bar.progress,
                    saturation_seek_bar.progress,
                    lightness_seek_bar.progress
                )
                ledLamp.setColor(color)
            }
        }

        hue_seek_bar.setOnSeekBarChangeListener(seekbarListener)
        saturation_seek_bar.setOnSeekBarChangeListener(seekbarListener)
        lightness_seek_bar.setOnSeekBarChangeListener(seekbarListener)

        hue_seek_bar.isEnabled = false
        saturation_seek_bar.isEnabled = false
        lightness_seek_bar.isEnabled = false
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
        if (ledLamp.hasNoDevice()) {
            Log.i(TAG, "Start Scan")
            bluetoothLeScanner.startScan(
                mutableListOf<ScanFilter>(lampScanFilter),
                ScanSettings.Builder().build(),
                scanCallback
            )
        } else if (ledLamp.hasDeviceButDisconnected()) {
            ledLamp.resumeConnection()
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
        ledLamp.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        ledLamp.destroy()
    }

}
