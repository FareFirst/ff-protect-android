package com.amahop.farefirst.ffprotect.sync

import android.content.Context
import android.location.Location
import android.util.Log
import com.amahop.farefirst.ffprotect.db.DBProvider
import com.amahop.farefirst.ffprotect.sync.network.SyncService
import com.amahop.farefirst.ffprotect.sync.network.pojo.SyncData
import com.amahop.farefirst.ffprotect.utils.AuthManger
import com.amahop.farefirst.ffprotect.utils.LocationHelper
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
import com.amahop.farefirst.ffprotect.utils.Settings
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class SyncManger(private val context: Context) {
    companion object {
        const val TAG = "SyncManger"
    }

    fun sync(listener: (isSuccess: Boolean) -> Unit) {
        AuthManger.getCurrentUser()?.let { currentUser ->
            AuthManger.getBearerToken() { bearerToken ->
                if (bearerToken == null) {
                    Log.e(TAG, "Bearer token is null")
                    listener(false)
                    return@getBearerToken
                }

                LocationHelper.requestLocation(this.context) { location ->
                    doSync(currentUser, bearerToken, location, listener)
                }
            }
        } ?: kotlin.run {
            Log.e(TAG, "Current user is null")
            listener(false)
        }
    }

    private fun doSync(
        currentUser: FirebaseUser,
        bearerToken: String,
        location: Location?,
        listener: (isSuccess: Boolean) -> Unit
    ) {
        val fcmToken = getFCMToken()

        val syncTime = System.currentTimeMillis()

        val trackers = DBProvider.getDB(this.context).trackerDao().trackersSync(syncTime)

        val loc = if (location != null) {
            com.amahop.farefirst.ffprotect.sync.network.Location(
                latitude = location.latitude,
                longitude = location.longitude
            )
        } else {
            null
        }

        val syncData = SyncData(
            clientUid = currentUser.uid,
            location = loc,
            fcmToken = fcmToken,
            trackers = trackers
        )

        getSyncService().sync(bearerToken, syncData).enqueue(object : Callback<Response<Void>> {
            override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                Log.e(TAG, "Failed to sync ${t.message}")
                listener(false)
            }

            override fun onResponse(
                call: Call<Response<Void>>,
                response: Response<Response<Void>>
            ) {
                when (response.code()) {
                    401 -> {
                        Log.e(TAG, "Sync failed with ${response.code()} error")
                        AuthManger.getBearerToken(true) {
                            Log.d(TAG, "Token force refreshed")
                            listener(false)
                        }
                    }
                    200 -> {
                        GlobalScope.launch() {
                            DBProvider.getDB(context).trackerDao().deleteSynced(syncTime)
                        }
                        listener(true)
                    }
                    else -> {
                        Log.e(TAG, "Sync failed with ${response.code()} error")
                        listener(false)
                    }
                }
            }

        })
    }

    private fun getSyncService(): SyncService {
        val retrofit = Retrofit.Builder()
            .baseUrl(RemoteConfigManager.getSyncBaseUrl())
            .build()

        return retrofit.create(SyncService::class.java)
    }

    private fun getFCMToken(): String? {
        val fcmToken = Settings.getFCMToken()
        if (fcmToken == null) {
            Log.d(TAG, "FCM token is null")
        }

        return fcmToken
    }
}