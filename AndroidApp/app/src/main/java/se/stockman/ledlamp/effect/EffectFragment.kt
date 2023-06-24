package se.stockman.ledlamp.effect

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.stockman.ledlamp.BaseFragment
import se.stockman.ledlamp.databinding.FragmentEffectBinding


class EffectFragment : BaseFragment() {
    private var _binding: FragmentEffectBinding? = null
    private val binding get() = _binding!!
    override fun onConnectionStateChange(connected: Boolean) {

    }

    private var listener: OnEffectSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEffectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gridview.adapter = EffectAdapter()
        binding.gridview.setOnItemClickListener { _, _, _, id -> listener?.onEffectSelected(id.toInt()) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEffectSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
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
