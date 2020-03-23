package com.amahop.farefirst.ffprotect.utils

import android.util.Log
import com.crashlytics.android.Crashlytics

object LogManager {

    private const val LOG_NAME_SPACE = "FFProtect"

    fun e(tag: String?, message: String) {
        Log.e(getTag(tag), message)
    }

    fun e(tag: String?, message: String?, throwable: Throwable?) {
        Log.e(getTag(tag), message, throwable)
        handleThrowable(throwable)
    }

    fun d(tag: String?, message: String) {
        Log.d(getTag(tag), message)
    }

    fun d(tag: String?, message: String?, throwable: Throwable?) {
        Log.d(getTag(tag), message, throwable)
        handleThrowable(throwable)
    }

    fun w(tag: String?, message: String) {
        Log.w(getTag(tag), message)
    }

    fun w(tag: String?, message: String?, throwable: Throwable?) {
        Log.w(getTag(tag), message, throwable)
        handleThrowable(throwable)
    }

    fun i(tag: String?, message: String) {
        Log.i(getTag(tag), message)
    }

    fun i(tag: String?, message: String?, throwable: Throwable?) {
        Log.i(getTag(tag), message, throwable)
        handleThrowable(throwable)
    }

    fun v(tag: String?, message: String) {
        Log.v(getTag(tag), message)
    }

    fun v(tag: String?, message: String?, throwable: Throwable?) {
        Log.v(getTag(tag), message, throwable)
        handleThrowable(throwable)
    }

    private fun handleThrowable(throwable: Throwable?) {
        if (throwable != null) {
            Crashlytics.logException(throwable)
        }
    }


    private fun getTag(tag: String?): String {
        return if (tag != null) {
            "$LOG_NAME_SPACE-$tag"
        } else {
            return LOG_NAME_SPACE
        }
    }
}