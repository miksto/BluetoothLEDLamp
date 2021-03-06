package se.stockman.ledlamp.color

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_color.*
import se.stockman.ledlamp.BaseFragment
import se.stockman.ledlamp.R
import se.stockman.ledlamp.data.RgbColor

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

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, userInitiated: Boolean) {
            // Display the current progress of SeekBar
            if (userInitiated) {
                val color = RgbColor(
                    seek_bar_color_red.progress,
                    seek_bar_color_green.progress,
                    seek_bar_color_blue.progress
                )
                listener?.onSetColor(color)
            }
        }
    }

    private fun updateView() {
        if (view == null) {
            return
        }
        seek_bar_color_red?.isEnabled = connected
        seek_bar_color_green?.isEnabled = connected
        seek_bar_color_blue?.isEnabled = connected

        currentColor?.let {
            seek_bar_color_red?.progress = it.red
            seek_bar_color_green?.progress = it.green
            seek_bar_color_blue?.progress = it.blue
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
        seek_bar_color_red.setOnSeekBarChangeListener(seekbarListener)
        seek_bar_color_green.setOnSeekBarChangeListener(seekbarListener)
        seek_bar_color_blue.setOnSeekBarChangeListener(seekbarListener)
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
