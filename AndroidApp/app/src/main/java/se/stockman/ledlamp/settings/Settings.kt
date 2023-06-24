package se.stockman.ledlamp.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by Mikael Stockman on 2019-10-07.
 */
class Settings {
    companion object {
        private const val SPOTIFY_ENABLED = "SPOTIFY_ENABLED"
        private const val NOTIFICATION_FLASH_ENABLED = "NOTIFICATION_FLASH_ENABLED"

        fun getSharedPrefs(context: Context): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun setSpotifyIntegrationEnabled(context: Context, enabled: Boolean) {
            getSharedPrefs(context)!!.edit().putBoolean(SPOTIFY_ENABLED, enabled)?.apply()
        }

        fun isSpotifyIntegrationEnabled(context: Context): Boolean {
            return getSharedPrefs(context)?.getBoolean(SPOTIFY_ENABLED, true)!!
        }

        fun setNotificationFlashEnabled(context: Context, enabled: Boolean) {
            getSharedPrefs(context)!!.edit().putBoolean(NOTIFICATION_FLASH_ENABLED, enabled)
                ?.apply()
        }

        fun isNotificationFlashEnabled(context: Context): Boolean {
            return getSharedPrefs(context)?.getBoolean(NOTIFICATION_FLASH_ENABLED, false)!!
        }
    }
}