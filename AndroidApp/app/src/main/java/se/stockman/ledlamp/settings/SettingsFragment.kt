package se.stockman.ledlamp.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.stockman.ledlamp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.spotifySwitch.isChecked = Settings.isSpotifyIntegrationEnabled(view.context)
        binding.spotifySwitch.setOnCheckedChangeListener { switch, isChecked ->
            Settings.setSpotifyIntegrationEnabled(switch.context, isChecked)
        }
        binding.notificationFlashSwitch.setOnCheckedChangeListener { switch, isChecked ->
            Settings.setNotificationFlashEnabled(switch.context, isChecked)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

}
