package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.telephony.TelephonyManager
import java.util.*

object SystemUtils {
    const val TAG = "SystemUtils"

    fun getNetworkCountryCode(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (tm == null) {
            LogManager.d(TAG, "TELEPHONY_SERVICE is null")
            return null
        }

        if (tm.networkCountryIso == null) {
            LogManager.d(TAG, "networkCountryIso is null")
            return null
        }

        return tm.networkCountryIso.toUpperCase(Locale.US)
    }

    fun getSimCountryCode(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?

        if (tm == null) {
            LogManager.d(TAG, "TELEPHONY_SERVICE is null")
            return null
        }

        if (tm.simCountryIso == null) {
            LogManager.d(TAG, "simCountryIso is null")
            return null
        }

        return tm.simCountryIso.toUpperCase(Locale.US)
    }
}