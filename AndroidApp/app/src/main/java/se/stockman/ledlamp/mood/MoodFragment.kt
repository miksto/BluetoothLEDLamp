package se.stockman.ledlamp.mood

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.stockman.ledlamp.BaseFragment
import se.stockman.ledlamp.databinding.FragmentMoodBinding

class MoodFragment : BaseFragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    override fun onConnectionStateChange(connected: Boolean) {

    }

    private var listener: OnMoodSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gridview.adapter = MoodAdapter()
        binding.gridview.setOnItemClickListener { _, _, _, id ->
            listener?.onMoodSelected(id.toInt())
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
