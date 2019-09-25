package se.stockman.ledlamp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_color.*

class ColorFragment : BaseFragment() {
    var currentColor: RgbColor? = null
    var connected = false

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

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            // Display the current progress of SeekBar
            val brightnessFactor = seekbar_brighness.progress / 100f

            val color = RgbColor(
                (seekbar_color_red.progress * brightnessFactor).toInt(),
                (seekbar_color_green.progress * brightnessFactor).toInt(),
                (seekbar_color_blue.progress * brightnessFactor).toInt()
            )
            listener?.onSetColor(color)
        }
    }

    private fun updateView() {
        if (view == null) {
            return
        }
        seekbar_color_red?.isEnabled = connected
        seekbar_color_green?.isEnabled = connected
        seekbar_color_blue?.isEnabled = connected
        seekbar_brighness?.isEnabled = connected

        currentColor?.let {
            seekbar_color_red.setOnSeekBarChangeListener(null)
            seekbar_color_green.setOnSeekBarChangeListener(null)
            seekbar_color_blue.setOnSeekBarChangeListener(null)
            seekbar_brighness.setOnSeekBarChangeListener(null)


            seekbar_color_red?.progress = it.red
            seekbar_color_green?.progress = it.green
            seekbar_color_blue?.progress = it.blue
            seekbar_brighness?.progress = seekbar_brighness.max


            seekbar_color_red.setOnSeekBarChangeListener(seekbarListener)
            seekbar_color_green.setOnSeekBarChangeListener(seekbarListener)
            seekbar_color_blue.setOnSeekBarChangeListener(seekbarListener)
            seekbar_brighness.setOnSeekBarChangeListener(seekbarListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        seekbar_color_red.setOnSeekBarChangeListener(seekbarListener)
        seekbar_color_green.setOnSeekBarChangeListener(seekbarListener)
        seekbar_color_blue.setOnSeekBarChangeListener(seekbarListener)
        seekbar_brighness.setOnSeekBarChangeListener(seekbarListener)

        debug_button.setOnClickListener {
            listener?.onDebugButtonPressed()
        }
        updateView()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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

    interface OnFragmentInteractionListener {
        fun onSetColor(color: RgbColor)
        fun onDebugButtonPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ColorFragment()
    }
}
