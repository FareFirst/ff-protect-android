package com.amahop.farefirst.ffprotect.sync.repositories.pojos

import android.os.Build
import com.amahop.farefirst.ffprotect.BuildConfig
import java.util.*

data class Device(
    val fcmToken: String?,
    val networkCountryCode: String?,
    val simCountryCode: String?
) {
    val platform: String = "android"
    val appVersionCode: Int = BuildConfig.VERSION_CODE
    val osVersionCode: Int = Build.VERSION.SDK_INT
    val locale: String = Locale.getDefault().toLanguageTag()
    val modelInfo: ModelInfo = ModelInfo()
}