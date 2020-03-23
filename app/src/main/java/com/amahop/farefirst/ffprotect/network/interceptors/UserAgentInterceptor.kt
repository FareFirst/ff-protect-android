package com.amahop.farefirst.ffprotect.network.interceptors

import android.os.Build
import com.amahop.farefirst.ffprotect.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class UserAgentInterceptor(appName: String) : Interceptor {

    private val userAgent: String = String.format(
        "%s/%s (Android %s; %s; %s %s; %s)",
        appName,
        BuildConfig.VERSION_CODE.toString(),
        Build.VERSION.SDK_INT,
        Build.MODEL,
        Build.BRAND,
        Build.DEVICE,
        Locale.getDefault().language
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(userAgentRequest)
    }
}