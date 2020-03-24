package com.amahop.farefirst.ffprotect.utils

import com.amahop.farefirst.ffprotect.BuildConfig
import com.amahop.farefirst.ffprotect.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.concurrent.TimeUnit

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    @Synchronized
    fun init(listener: () -> Unit) {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                TimeUnit.HOURS.toSeconds(2)
            } else {
                TimeUnit.HOURS.toSeconds(24)
            }
            fetchTimeoutInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
                    .addOnCompleteListener { setDefaultTask ->
                        if (setDefaultTask.isSuccessful) {
                            remoteConfig.fetchAndActivate()
                                .addOnCompleteListener { serverValueTask ->
                                    if (serverValueTask.isSuccessful) {
                                        LogManager.d(
                                            TAG,
                                            "RemoteConfigManager successfully fetched and activated"
                                        )
                                    } else {
                                        LogManager.d(
                                            TAG,
                                            "Failed to fetch and activate server values"
                                        )
                                    }
                                    listener()
                                }
                        } else {
                            val th =
                                RuntimeException("Failed to activate remote config default value")
                            LogManager.e(TAG, th.message, th)
                            listener()
                        }
                    }
            } else {
                val th = RuntimeException("Failed to activate remote config settings")
                LogManager.e(TAG, th.message, th)

                listener()
            }
        }
    }


    private const val RC_KEY_IS_APP_BLOCKED = "is_block_app"
    private const val RC_KEY_FOREGROUND_BETWEEN_SCAN_PERIOD = "foreground_between_scan_period"
    private const val RC_KEY_FOREGROUND_SCAN_PERIOD = "foreground_scan_period"
    private const val RC_KEY_BACKGROUND_BETWEEN_SCAN_PERIOD = "background_between_scan_period"
    private const val RC_KEY_BACKGROUND_SCAN_PERIOD = "background_scan_period"
    private const val RC_KEY_SYNC_BASE_URL = "sync_base_url"
    private const val RC_KEY_TRACKER_WORKER_INTERVAL = "tracker_worker_interval"
    private const val RC_KEY_SYNC_WORKER_INTERVAL = "sync_worker_interval"
    private const val RC_KEY_PRIVACY_URL = "privacy_url"
    private const val RC_KEY_TERMS_URL = "terms_url"
    private const val RC_KEY_HOW_IT_WORKS_URL = "how_it_works_url"
    private const val RC_KEY_APP_SHARE_URL = "app_share_url"

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

    fun getSyncBaseUrl(): String {
        return Firebase.remoteConfig.getString(RC_KEY_SYNC_BASE_URL)
    }

    fun getTrackerWorkerIntervalInMinutes(): Long {
        return Firebase.remoteConfig.getLong(RC_KEY_TRACKER_WORKER_INTERVAL)
    }

    fun getSyncWorkerIntervalInMinutes(): Long {
        return Firebase.remoteConfig.getLong(RC_KEY_SYNC_WORKER_INTERVAL)
    }

    fun getPrivacyUrl(): String {
        return Firebase.remoteConfig.getString(RC_KEY_PRIVACY_URL)
    }

    fun getTermsUrl(): String {
        return Firebase.remoteConfig.getString(RC_KEY_TERMS_URL)
    }

    fun getHowItWorksUrl(): String {
        return Firebase.remoteConfig.getString(RC_KEY_HOW_IT_WORKS_URL)
    }

    fun getAppShareUrl(): String {
        return Firebase.remoteConfig.getString(RC_KEY_APP_SHARE_URL)
    }


}