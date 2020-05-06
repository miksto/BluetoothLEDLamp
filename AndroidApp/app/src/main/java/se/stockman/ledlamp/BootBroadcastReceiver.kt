package se.stockman.ledlamp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Mikael Stockman on 2019-09-07.
 */
@ExperimentalUnsignedTypes
class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED
            || intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            context?.startService(NotificationListener.createIntent(context))
        }
    }
}