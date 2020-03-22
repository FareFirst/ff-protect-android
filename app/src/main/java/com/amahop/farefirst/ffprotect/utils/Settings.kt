package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.content.SharedPreferences
import com.amahop.farefirst.ffprotect.BuildConfig


object Settings {
    private const val TAG = "Settings"

    private const val PREF_KEY_NO_BLUETOOTH_NOTIFICATION = "no_bluetooth_notification"

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
}