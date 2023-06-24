package se.stockman.ledlamp.color

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import se.stockman.ledlamp.BaseFragment
import se.stockman.ledlamp.data.RgbColor
import se.stockman.ledlamp.databinding.FragmentColorBinding

class ColorFragment : BaseFragment() {
    private var _binding: FragmentColorBinding? = null
    private val binding get() = _binding!!

    private var currentColor: RgbColor? = null
    private var connected = false

    override fun onConnectionStateChange(connected: Boolean) {
        this@ColorFragment.connected = connected
        view?.post { updateView() }
    }

    private var listener: OnFragmentInteractionListener? = null

    private val seekbarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, userInitiated: Boolean) {
            // Display the current progress of SeekBar
            if (userInitiated) {
                val color = RgbColor(
                    binding.seekBarColorRed.progress,
                    binding.seekBarColorGreen.progress,
                    binding.seekBarColorBlue.progress
                )
                listener?.onSetColor(color)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.seekBarColorRed.setOnSeekBarChangeListener(seekbarListener)
        binding.seekBarColorGreen.setOnSeekBarChangeListener(seekbarListener)
        binding.seekBarColorBlue.setOnSeekBarChangeListener(seekbarListener)
        updateView()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun setColor(color: RgbColor) {
        currentColor = color
        view?.post { updateView() }
    }


    private fun updateView() {
        if (view == null) {
            return
        }
        binding.seekBarColorRed.isEnabled = connected
        binding.seekBarColorGreen.isEnabled = connected
        binding.seekBarColorBlue.isEnabled = connected

        currentColor?.let {
            binding.seekBarColorRed.progress = it.red
            binding.seekBarColorGreen.progress = it.green
            binding.seekBarColorBlue.progress = it.blue
        }
    }

    interface OnFragmentInteractionListener {
        fun onSetColor(color: RgbColor)
        fun onDebugButtonPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ColorFragment()
    }
}
