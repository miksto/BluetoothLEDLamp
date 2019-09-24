package se.stockman.ledlamp

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        val TAG: String? = MainActivity::class.simpleName
    }

    val handler = Handler()

    val seekbarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            // Display the current progress of SeekBar
            val brightnessFactor = seekbar_brighness.progress / 100f

            val color = RgbColor(
                (seekbar_color_red.progress * brightnessFactor).toInt(),
                (seekbar_color_green.progress * brightnessFactor).toInt(),
                (seekbar_color_blue.progress * brightnessFactor).toInt()
            )
            ledLamp.setColor(color)
        }
    }

    private val lampCallback = object : LedLamp.LampCallback {
        override fun onConnectionStateChange(connected: Boolean) {
            handler.post {
                seekbar_color_red.isEnabled = connected
                seekbar_color_green.isEnabled = connected
                seekbar_color_blue.isEnabled = connected
                seekbar_brighness.isEnabled = connected
            }
        }

        override fun onColorChanged(color: RgbColor) {
            seekbar_color_red.progress = color.red
            seekbar_color_green.progress = color.green
            seekbar_color_blue.progress = color.blue
            seekbar_brighness.progress = seekbar_brighness.max
        }
    }

    private val lampDeviceFoundCallback = object : LampDeviceDiscovery.Callback {
        override fun onDeviceFound(device: BluetoothDevice) {
            lampFinder.stop()
            if (ledLamp.hasNoDevice()) {
                ledLamp.connectToDevice(device, this@MainActivity)
            }
        }
    }

    private val lampFinder: LampDeviceDiscovery = LampDeviceDiscovery(lampDeviceFoundCallback)
    private var ledLamp: LedLamp = LedLamp(this, lampCallback)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(NotificationListener.createIntent(this))

        seekbar_color_red.setOnSeekBarChangeListener(seekbarListener)
        seekbar_color_green.setOnSeekBarChangeListener(seekbarListener)
        seekbar_color_blue.setOnSeekBarChangeListener(seekbarListener)
        seekbar_brighness.setOnSeekBarChangeListener(seekbarListener)

        seekbar_color_red.isEnabled = false
        seekbar_color_green.isEnabled = false
        seekbar_color_blue.isEnabled = false
        seekbar_brighness.isEnabled = false

        debug_button.setOnClickListener {
            ledLamp.callDebugFunction()
        }
    }

    override fun onStart() {
        super.onStart()
        val permission = PermissionChecker.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val notificationAccessEnabled =
            NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
        if (!notificationAccessEnabled) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        } else {
            when (permission) {
                PermissionChecker.PERMISSION_GRANTED -> connectToLampIfNecessary()
                else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
        }
    }

    private fun connectToLampIfNecessary() {
        if (ledLamp.hasNoDevice()) {
            Log.i(TAG, "Start Scan")
            lampFinder.findDevice()
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
                    lampFinder.findDevice()
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
        super.onStop()
        lampFinder.stop()
        ledLamp.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        ledLamp.destroy()
    }

}
