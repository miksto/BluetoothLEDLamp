package se.stockman.ledlamp.effect

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_effect.*
import se.stockman.ledlamp.R


class EffectFragment : Fragment() {
    private var listener: OnEffectSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_effect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridview.adapter = EffectAdapter()
        gridview.setOnItemClickListener { _, _, _, id -> listener!!.onEffectSelected(id.toInt()) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEffectSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnEffectSelectedListener {
        fun onEffectSelected(effectId: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EffectFragment()
    }
}
