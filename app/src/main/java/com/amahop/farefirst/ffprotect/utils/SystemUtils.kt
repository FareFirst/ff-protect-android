package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.content.res.Configuration
import android.telephony.TelephonyManager
import com.amahop.farefirst.ffprotect.R
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

    fun isLandscapeOrientation(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun getStatusBarHeight(context: Context): Float {
        return context.resources.getDimension(R.dimen.android_status_bar_height)
    }
}