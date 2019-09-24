package se.stockman.ledlamp

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker


class MainActivity : ColorFragment.OnFragmentInteractionListener, AppCompatActivity() {

    val colorFragment = ColorFragment.newInstance()

    override fun onSetColor(color: RgbColor) {
        ledLamp.setColor(color)
    }

    override fun onDebugButtonPressed() {
        ledLamp.callDebugFunction()
    }


    companion object {
        val TAG: String? = MainActivity::class.simpleName
    }

    val handler = Handler()


    private val lampCallback = object : LedLamp.LampCallback {
        override fun onConnectionStateChange(connected: Boolean) {
            colorFragment.onConnectionStateChange(connected)
        }

        override fun onColorChanged(color: RgbColor) {
            colorFragment.setColor(color)
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

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, colorFragment)
        transaction.commit()
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
