package se.stockman.ledlamp

import androidx.fragment.app.Fragment

/**
 * Created by Mikael Stockman on 2019-09-24.
 */

abstract class BaseFragment : Fragment() {
    abstract fun onConnectionStateChange(connected: Boolean)
}