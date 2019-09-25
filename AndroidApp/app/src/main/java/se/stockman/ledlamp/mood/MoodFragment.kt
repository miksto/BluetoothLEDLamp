package se.stockman.ledlamp.mood

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_effect.*
import se.stockman.ledlamp.BaseFragment
import se.stockman.ledlamp.R

class MoodFragment : BaseFragment() {
    override fun onConnectionStateChange(connected: Boolean) {

    }

    private var listener: OnMoodSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridview.adapter = MoodAdapter()
        gridview.setOnItemClickListener { _, _, _, id ->
            listener!!.onMoodSelected(id.toInt())
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMoodSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnMoodSelectedListener {
        fun onMoodSelected(effectId: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MoodFragment()
    }
}
