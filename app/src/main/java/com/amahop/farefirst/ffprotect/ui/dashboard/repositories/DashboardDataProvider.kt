package com.amahop.farefirst.ffprotect.ui.dashboard.repositories

import com.amahop.farefirst.ffprotect.network.ApiServiceFactory
import com.amahop.farefirst.ffprotect.ui.dashboard.repositories.pojos.GovMessage
import com.amahop.farefirst.ffprotect.utils.AuthManger
import com.amahop.farefirst.ffprotect.utils.LocaleProvider
import com.amahop.farefirst.ffprotect.utils.LogManager
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DashboardDataProvider {

    const val TAG = "DashboardDataProvider"

    fun fetchGovMessage(listener: (govMessage: GovMessage?) -> Unit) {
        AuthManger.getBearerToken { bearerToken ->
            if (bearerToken == null) {
                LogManager.e(TAG, "Bearer token is null")
                listener(null)
                return@getBearerToken
            }

            getService().fetchGovMessage(bearerToken, LocaleProvider.getLocaleForApi())
                .enqueue(object :
                    Callback<GovMessage> {
                    override fun onFailure(call: Call<GovMessage>, t: Throwable) {
                        LogManager.e(TAG, "Gov message fetch failed with message => ${t.message}")
                        listener(null)
                    }

                    override fun onResponse(
                        call: Call<GovMessage>,
                        response: Response<GovMessage>
                    ) {
                        when (response.code()) {
                            401 -> {
                                LogManager.e(
                                    TAG,
                                    "Gov message fetch failed with ${response.code()} error"
                                )
                                AuthManger.getBearerToken(true) {
                                    LogManager.d(TAG, "Token force refreshed")
                                    listener(null)
                                }
                            }
                            in 200..299 -> {
                                listener(response.body())
                            }
                            else -> {
                                LogManager.e(
                                    TAG,
                                    "Gov message fetch failed with ${response.code()} error"
                                )
                                listener(null)
                            }
                        }
                    }

                })
        }
    }

    private fun getService(): DashboardService {
        return ApiServiceFactory.getService(
            RemoteConfigManager.getDashboardBaseUrl(),
            DashboardService::class.java
        )
    }
}