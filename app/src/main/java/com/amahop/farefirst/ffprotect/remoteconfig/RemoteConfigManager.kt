package com.amahop.farefirst.ffprotect.remoteconfig

import android.util.Log
import com.amahop.farefirst.ffprotect.BuildConfig
import com.amahop.farefirst.ffprotect.R
import com.crashlytics.android.Crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.concurrent.TimeUnit

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    fun init(listener: () -> Unit) {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                TimeUnit.HOURS.toSeconds(24)
            } else {
                0
            }
        }
        remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
                    .addOnCompleteListener { setDefaultTask ->
                        if (setDefaultTask.isSuccessful) {
                            remoteConfig.fetchAndActivate()
                                .addOnCompleteListener { serverValueTask ->
                                    if (!serverValueTask.isSuccessful) {
                                        Log.d(TAG, "Failed to fetch and activate server values")
                                    }
                                    listener()
                                }
                        } else {
                            Crashlytics.logException(RuntimeException("Failed to activate remote config default value"))
                            listener()
                        }
                    }
            } else {
                Crashlytics.logException(RuntimeException("Failed to activate remote config settings"))
                listener()
            }
        }
    }


    private const val RC_KEY_IS_APP_BLOCKED = "is_block_app"
    private const val RC_KEY_FOREGROUND_BETWEEN_SCAN_PERIOD = "foreground_between_scan_period"
    private const val RC_KEY_FOREGROUND_SCAN_PERIOD = "foreground_scan_period"
    private const val RC_KEY_BACKGROUND_BETWEEN_SCAN_PERIOD = "background_between_scan_period"
    private const val RC_KEY_BACKGROUND_SCAN_PERIOD = "background_scan_period"

    fun isAppBlocked(): Boolean {
        return Firebase.remoteConfig.getBoolean(RC_KEY_IS_APP_BLOCKED)
    }

    fun getForegroundBetweenScanPeriod(): Long {
        return Firebase.remoteConfig.getLong(RC_KEY_FOREGROUND_BETWEEN_SCAN_PERIOD)
    }

    fun getForegroundScanPeriod(): Long {
        return Firebase.remoteConfig.getLong(RC_KEY_FOREGROUND_SCAN_PERIOD)
    }

    fun getBackgroundBetweenScanPeriod(): Long {
        return Firebase.remoteConfig.getLong(RC_KEY_BACKGROUND_BETWEEN_SCAN_PERIOD)
    }

    fun getBackgroundScanPeriod(): Long {
        return Firebase.remoteConfig.getLong(RC_KEY_BACKGROUND_SCAN_PERIOD)
    }
}