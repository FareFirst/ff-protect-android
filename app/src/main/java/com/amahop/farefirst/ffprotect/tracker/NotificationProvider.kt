package com.amahop.farefirst.ffprotect.tracker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.amahop.farefirst.ffprotect.MainActivity
import com.amahop.farefirst.ffprotect.R


const val TRACKER_RUNNING_NOTIFICATION_ID = 999

fun getTrackerRunningNotification(context: Context): Notification {
    val builder =
        NotificationCompat.Builder(context, getNotificationChannelId(context))
            .setSmallIcon(R.drawable.ic_ff_notification)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.tracking_service_notification_text))

    builder.setContentIntent(getPendingIntent(context))
    builder.setDefaults(Notification.DEFAULT_ALL)

    return builder.build()
}

private fun getNotificationChannelId(context: Context): String {
    return context.getString(R.string.app_bg_service_notification_channel_id)
}

private fun getPendingIntent(
    context: Context
): PendingIntent? {
    val resultIntent = Intent(context, MainActivity::class.java)
    resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    return PendingIntent.getActivity(
        context, TRACKER_RUNNING_NOTIFICATION_ID, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}