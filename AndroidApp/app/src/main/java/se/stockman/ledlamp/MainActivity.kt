package se.stockman.ledlamp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
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

    private val handler = Handler(Looper.getMainLooper())
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

        private val bluetoothPermissions = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )

        const val REQUEST_STORAGE_PERMISSION = 1
        const val REQUEST_BLUETOOTH_PERMISSION = 3
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

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        it
                    )
                    ledLamp.setEffect(LampEffect.fromBitmap(bitmap))
                }
            }
        }

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
        checkPermissionsAndConnect()
    }

    private fun checkPermissionsAndConnect() {
        when {
            !isNotificationAccessGranted() -> startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            !isAllBluetoothPermissionsGranted() -> requestAllBluetoothPermissions()
            else -> connectToLampIfNecessary()
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

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        imagePickerResult.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> checkPermissionsAndConnect()

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

    override fun onStop() {
        super.onStop()
        lampFinder.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ledLamp.disconnect()
        ledLamp.destroy()
    }

    private fun isAllBluetoothPermissionsGranted() = bluetoothPermissions.all {
        PermissionChecker.checkSelfPermission(this, it) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun requestAllBluetoothPermissions() {
        requestPermissions(
            getMissingPermission().toTypedArray(),
            REQUEST_BLUETOOTH_PERMISSION
        )
    }

    private fun getMissingPermission() = bluetoothPermissions.filterNot {
        PermissionChecker.checkSelfPermission(this, it) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun isNotificationAccessGranted() =
        NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
}
