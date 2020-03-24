package com.amahop.farefirst.ffprotect.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


object DateHelper {
    fun getDateTimeString(
        time: Long,
        dateFormat: String
    ): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return formatter.format(calendar.time)
    }

    fun getRelativeDateTime(time: Long): String {
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString()
    }
}