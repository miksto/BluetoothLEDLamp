package se.stockman.ledlamp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import se.stockman.ledlamp.color.ColorFragment
import se.stockman.ledlamp.data.LampEffect
import se.stockman.ledlamp.data.RgbColor
import se.stockman.ledlamp.databinding.ActivityMainBinding
import se.stockman.ledlamp.databinding.BottomMenuBinding
import se.stockman.ledlamp.effect.EffectFragment
import se.stockman.ledlamp.mood.MoodFragment


@ExperimentalUnsignedTypes
class MainActivity : ColorFragment.OnFragmentInteractionListener,
    EffectFragment.OnEffectSelectedListener, MoodFragment.OnMoodSelectedListener,
    AppCompatActivity() {
    override fun onMoodSelected(effectId: Int) {
        val effect = LampEffect.moodFromId(effectId)
        ledLamp.setEffect(effect)
    }

    override fun onEffectSelected(effectId: Int) {
        val effect = LampEffect.effectFromId(effectId)
        ledLamp.setEffect(effect)
    }

    private val handler = Handler()
    private val colorFragment = ColorFragment.newInstance()
    private val effectFragment = EffectFragment.newInstance()
    private val moodFragment = MoodFragment.newInstance()

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

    }

    override fun onSetColor(color: RgbColor) {
        ledLamp.setColor(color)
    }

    override fun onDebugButtonPressed() {
        ledLamp.callDebugFunction()
    }


    companion object {
        val TAG: String? = MainActivity::class.simpleName
        const val REQUEST_GET_SINGLE_FILE = 1
        const val REQUEST_LOCATION_PERMISSION = 2
        const val REQUEST_STORAGE_PERMISSION = 3
        const val REQUEST_BL_SCAN_PERMISSION = 4
        const val REQUEST_BL_CONNECT_PERMISSION = 5
    }


    private val lampCallback = object : LedLamp.LampCallback {
        override fun onDimFactorReceived(dimFactor: Int) {
            binding.seekBarBrightness.progress = dimFactor
        }

        override fun onConnectionStateChange(connected: Boolean) {
            handler.post {
                if (connected) {
                    binding.loadingOverlay.visibility = View.GONE
                } else {
                    binding.loadingOverlay.visibility = View.VISIBLE
                }
            }
            colorFragment.onConnectionStateChange(connected)
            effectFragment.onConnectionStateChange(connected)
            moodFragment.onConnectionStateChange(connected)
        }

        override fun onColorReceived(color: RgbColor) {
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

    private val seekbarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(p0: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekBar?) {}
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, userInitiated: Boolean) {
            if (userInitiated) {
                ledLamp.setDimFactor(progress)
            }
        }
    }

    private val lampFinder: LampDeviceDiscovery = LampDeviceDiscovery(lampDeviceFoundCallback)
    private var ledLamp: LedLamp = LedLamp(this, lampCallback)
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomMenuBinding: BottomMenuBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bottomMenuBinding = BottomMenuBinding.bind(binding.bottomMenu.menuRoot)
        setContentView(binding.root)
        startService(NotificationListener.createIntent(this))

        setFragment(colorFragment)
        bottomMenuBinding.menuColorPicker.setOnClickListener { setFragment(colorFragment) }
        bottomMenuBinding.menuEffectPicker.setOnClickListener { setFragment(effectFragment) }
        bottomMenuBinding.menuMoodPicker.setOnClickListener { setFragment(moodFragment) }
        binding.seekBarBrightness.setOnSeekBarChangeListener(seekbarListener)

        binding.settingsIcon.setOnClickListener {
            startActivity(SettingsActivity.newIntent(this))
        }

        binding.galleryIcon.setOnClickListener {
            PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            val permission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            )
            when (permission) {
                PackageManager.PERMISSION_GRANTED -> openImagePicker()
                else -> requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    ), REQUEST_STORAGE_PERMISSION
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val notificationAccessEnabled =
            NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)

        if (!notificationAccessEnabled) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
            return
        }

        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_LOCATION_PERMISSION
            )
            return
        }

        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT,
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT
                ), REQUEST_BL_CONNECT_PERMISSION
            )
            return
        }

        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN,
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN
                ), REQUEST_BL_SCAN_PERMISSION
            )
            return
        }

        connectToLampIfNecessary()
    }

    private fun connectToLampIfNecessary() {
        if (ledLamp.hasNoDevice()) {
            Log.i(TAG, "Start Scan")
            lampFinder.findDevice()
        } else if (ledLamp.hasDeviceButDisconnected()) {
            ledLamp.resumeConnection()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.title_select_image)),
            REQUEST_GET_SINGLE_FILE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION ->
                when (grantResults) {
                    intArrayOf(PackageManager.PERMISSION_GRANTED) ->
                        lampFinder.findDevice()

                    else ->
                        Log.d(
                            "ScanDeviceActivity",
                            "onRequestPermissionsResult(not PERMISSION_GRANTED)"
                        )
                }

            REQUEST_STORAGE_PERMISSION ->
                when (grantResults) {
                    intArrayOf(PackageManager.PERMISSION_GRANTED) -> openImagePicker()
                    else -> Log.d(
                        "ScanDeviceActivity",
                        "onRequestPermissionsResult(not PERMISSION_GRANTED)"
                    )
                }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GET_SINGLE_FILE ->
                when (resultCode) {

                    Activity.RESULT_OK ->
                        data?.data.let {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                contentResolver,
                                it
                            )
                            ledLamp.setEffect(LampEffect.fromBitmap(bitmap))
                        }

                    else -> Log.i(TAG, "Did not successfully retrieve image")
                }

            else -> Log.i(TAG, "Unknown request code in onActivityResult: $requestCode")
        }
    }

    override fun onStop() {
        super.onStop()
        lampFinder.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ledLamp.disconnect()
        ledLamp.destroy()
    }

}
