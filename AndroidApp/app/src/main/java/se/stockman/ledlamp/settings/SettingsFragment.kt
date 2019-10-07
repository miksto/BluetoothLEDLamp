package se.stockman.ledlamp.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*
import se.stockman.ledlamp.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spotify_switch.isChecked = Settings.isSpotifyIntegrationEnabled(view.context)
        spotify_switch.setOnCheckedChangeListener { switch, isChecked ->
            Settings.setSpotifyIntegrationEnabled(switch.context, isChecked)
        }
        notification_flash_switch.setOnCheckedChangeListener { switch, isChecked ->
            Settings.setNotificationFlashEnabled(switch.context, isChecked)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

}
