package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.content.SharedPreferences
import com.amahop.farefirst.ffprotect.BuildConfig


object Settings {
    private const val TAG = "Settings"

    const val PREF_KEY_IS_TRACKER_ON = "is_tracker_on"
    private const val PREF_KEY_ALLOW_TRACK_LOCATION = "allow_track_location"
    private const val PREF_KEY_IS_SHOW_BLUETOOTH_REQUIRED_NOTIFICATION =
        "is_show_bluetooth_required_notification"
    private const val PREF_KEY_FCM_TOKEN = "fcm_token"

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }

    fun getSP(): SharedPreferences {
        context?.let {
            return it.getSharedPreferences(
                "${BuildConfig.APPLICATION_ID}_preferences",
                Context.MODE_PRIVATE
            )
        } ?: kotlin.run {
            throw RuntimeException("Context is null")
        }
    }

    fun isTrackerOn(): Boolean {
        return getSP().getBoolean(PREF_KEY_IS_TRACKER_ON, true)
    }

    fun isAllowedToTrackLocation(): Boolean {
        return getSP().getBoolean(PREF_KEY_ALLOW_TRACK_LOCATION, true)
    }

    fun isAllowedToShowBluetoothNotification(): Boolean {
        return getSP().getBoolean(PREF_KEY_IS_SHOW_BLUETOOTH_REQUIRED_NOTIFICATION, true)
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