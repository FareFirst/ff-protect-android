package com.amahop.farefirst.ffprotect.tracker

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.amahop.farefirst.ffprotect.MainActivity
import com.amahop.farefirst.ffprotect.R
import com.amahop.farefirst.ffprotect.utils.BluetoothHelper
import com.amahop.farefirst.ffprotect.utils.Settings
import com.crashlytics.android.Crashlytics

private const val TAG = "NotificationProvider"
const val TRACKER_RUNNING_NOTIFICATION_ID = 999
const val BLUETOOTH_REQUIRED_NOTIFICATION_ID = 998

fun handleBluetoothRequiredNotification(context: Context, isForegroundRequest: Boolean) {
    getNotificationManager(context)?.cancel(BLUETOOTH_REQUIRED_NOTIFICATION_ID)

    if (isForegroundRequest) return

    if (BluetoothHelper.isBluetoothEnabled()) return

    if (!Settings.isAllowedToShowBluetoothNotification()) {
        Log.d(TAG, "Skipping bluetooth notification")
        return
    }

    showBluetoothRequiredNotification(context)
}

private fun showBluetoothRequiredNotification(context: Context) {
    val description = String.format(
        context.getString(R.string.turn_on_bluetooth_notification_description),
        context.getString(R.string.app_name)
    )

    val builder =
        NotificationCompat.Builder(
            context,
            context.getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_ff_notification)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.turn_on_bluetooth_notification_title))
            .setContentText(description)
            .setAutoCancel(true)

    builder.setStyle(NotificationCompat.BigTextStyle().bigText(description))

    builder.setContentIntent(
        getPendingIntentForBluetoothRequired(
            context,
            BLUETOOTH_REQUIRED_NOTIFICATION_ID
        )
    )
    builder.setDefaults(Notification.DEFAULT_ALL)

    getNotificationManager(context)?.notify(BLUETOOTH_REQUIRED_NOTIFICATION_ID, builder.build());
}

fun getTrackerRunningNotification(context: Context): Notification {
    val builder =
        NotificationCompat.Builder(
            context,
            context.getString(R.string.app_bg_service_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_ff_notification)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.tracking_service_notification_text))
            .setSound(null)

    builder.setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.tracking_service_notification_text)))

    builder.setContentIntent(getPendingIntent(context, TRACKER_RUNNING_NOTIFICATION_ID))
    builder.setDefaults(Notification.DEFAULT_ALL)

    return builder.build()
}

private fun getPendingIntent(
    context: Context,
    notificationId: Int
): PendingIntent? {
    val resultIntent = Intent(context, MainActivity::class.java)
    resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    return PendingIntent.getActivity(
        context, notificationId, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

private fun getPendingIntentForBluetoothRequired(
    context: Context,
    notificationId: Int
): PendingIntent? {
    val resultIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    return PendingIntent.getActivity(
        context, notificationId, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}


private fun getNotificationManager(context: Context): NotificationManager? {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    return if (notificationManager != null) {
        notificationManager
    } else {
        Crashlytics.logException(RuntimeException("The system service is null for " + Context.NOTIFICATION_SERVICE))
        null
    }
}