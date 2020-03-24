package com.amahop.farefirst.ffprotect.utils

import java.util.*

object LocaleProvider {
    fun getLocaleForApi(): String {
        return Locale.getDefault().toLanguageTag()
    }
}