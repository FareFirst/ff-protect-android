package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.content.SharedPreferences
import com.amahop.farefirst.ffprotect.BuildConfig


object Settings {
    private const val TAG = "Settings"

    private const val PREF_KEY_NO_BLUETOOTH_NOTIFICATION = "no_bluetooth_notification"
    private const val PREF_KEY_ALLOW_TRACK_LOCATION = "allow_track_location"
    private const val PREF_KEY_FCM_TOKEN = "fcm_token"

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }

    private fun getSP(): SharedPreferences {
        context?.let {
            return it.getSharedPreferences(
                BuildConfig.APPLICATION_ID,
                Context.MODE_PRIVATE
            )
        } ?: kotlin.run {
            throw RuntimeException("Context is null")
        }
    }

    fun setStopForBluetoothNotification(value: Boolean) {
        val editor = getSP().edit()
        editor.putBoolean(PREF_KEY_NO_BLUETOOTH_NOTIFICATION, value)
        editor.apply()
    }

    fun isAllowedToShowBluetoothNotification(): Boolean {
        return getSP().getBoolean(PREF_KEY_NO_BLUETOOTH_NOTIFICATION, true)
    }

    fun setAllowTrackLocation(value: Boolean) {
        val editor = getSP().edit()
        editor.putBoolean(PREF_KEY_ALLOW_TRACK_LOCATION, value)
        editor.apply()
    }

    fun isAllowedToTrackLocation(): Boolean {
        return getSP().getBoolean(PREF_KEY_ALLOW_TRACK_LOCATION, true)
    }

    fun setFCMToken(value: String) {
        val editor = getSP().edit()
        editor.putString(PREF_KEY_FCM_TOKEN, value)
        editor.apply()
    }

    fun getFCMToken(): String? {
        return getSP().getString(PREF_KEY_FCM_TOKEN, null)
    }
}