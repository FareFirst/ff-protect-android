package com.amahop.farefirst.ffprotect.sync

import android.content.Context
import android.location.Location
import com.amahop.farefirst.ffprotect.db.DBProvider
import com.amahop.farefirst.ffprotect.network.ApiServiceFactory
import com.amahop.farefirst.ffprotect.sync.repositories.SyncService
import com.amahop.farefirst.ffprotect.sync.repositories.pojos.Device
import com.amahop.farefirst.ffprotect.sync.repositories.pojos.SyncData
import com.amahop.farefirst.ffprotect.utils.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncManger(private val context: Context) {
    companion object {
        private const val TAG = "SyncManger"
    }

    fun sync(listener: (status: SyncWorker.Companion.ResultStatus) -> Unit) {
        AuthManger.getCurrentUser()?.let { currentUser ->
            AuthManger.getBearerToken { bearerToken ->
                if (bearerToken == null) {
                    LogManager.e(TAG, "Bearer token is null")
                    listener(SyncWorker.Companion.ResultStatus.FAILED)
                    return@getBearerToken
                }

                LocationHelper.requestLocation(this.context) { location ->
                    doSync(currentUser, bearerToken, location, listener)
                }
            }
        } ?: kotlin.run {
            LogManager.e(TAG, "Current user is null")
            listener(SyncWorker.Companion.ResultStatus.FAILED)
        }
    }

    private fun doSync(
        currentUser: FirebaseUser,
        bearerToken: String,
        location: Location?,
        listener: (status: SyncWorker.Companion.ResultStatus) -> Unit
    ) {
        GlobalScope.launch {
            val fcmToken = getFCMToken()

            val syncTime = System.currentTimeMillis()

            Settings.setLastSyncedAt(syncTime)

            val trackers = DBProvider.getDB(context).trackerDao().trackersToBeSync(syncTime)

            if (trackers.isEmpty()) {
                LogManager.d(TAG, "Nothing to sync")
                listener(SyncWorker.Companion.ResultStatus.SUCCESS)
                return@launch
            }

            val loc = if (location != null) {
                com.amahop.farefirst.ffprotect.sync.repositories.pojos.Location(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            } else {
                null
            }

            val device = Device(
                fcmToken = fcmToken,
                networkCountryCode = SystemUtils.getNetworkCountryCode(context),
                simCountryCode = SystemUtils.getSimCountryCode(context)
            )

            val syncData = SyncData(
                clientUserUid = currentUser.uid,
                location = loc,
                device = device,
                trackers = trackers
            )

            getSyncService().sync(bearerToken, syncData)
                .enqueue(object : Callback<Response<Void>> {
                    override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                        LogManager.e(TAG, "Failed to sync ${t.message}")
                        listener(SyncWorker.Companion.ResultStatus.FAILED)
                    }

                    override fun onResponse(
                        call: Call<Response<Void>>,
                        response: Response<Response<Void>>
                    ) {
                        when (response.code()) {
                            401 -> {
                                LogManager.e(TAG, "Sync failed with ${response.code()} error")
                                AuthManger.getBearerToken(true) {
                                    LogManager.d(TAG, "Token force refreshed")
                                    listener(SyncWorker.Companion.ResultStatus.RETRY)
                                }
                            }
                            in 200..299 -> {
                                GlobalScope.launch {
                                    DBProvider.getDB(context).trackerDao().deleteSynced(syncTime)
                                }
                                listener(SyncWorker.Companion.ResultStatus.SUCCESS)
                            }
                            else -> {
                                LogManager.e(TAG, "Sync failed with ${response.code()} error")
                                listener(SyncWorker.Companion.ResultStatus.FAILED)
                            }
                        }
                    }

                })
        }
    }

    private fun getSyncService(): SyncService {
        return ApiServiceFactory.getService(
            RemoteConfigManager.getSyncBaseUrl(),
            SyncService::class.java
        )
    }

    private fun getFCMToken(): String? {
        val fcmToken = Settings.getFCMToken()
        if (fcmToken == null) {
            LogManager.d(TAG, "FCM token is null")
        }

        return fcmToken
    }
}