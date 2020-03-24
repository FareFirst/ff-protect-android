package com.amahop.farefirst.ffprotect.utils

import com.amahop.farefirst.ffprotect.BuildConfig

object AppUtils {
    fun isProduction(): Boolean {
        return BuildConfig.BUILD_TYPE.equals("release")
    }
}