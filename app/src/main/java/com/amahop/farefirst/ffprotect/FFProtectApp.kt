package com.amahop.farefirst.ffprotect

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.altbeacon.beacon.powersave.BackgroundPowerSaver

class FFProtectApp : Application() {
    private var backgroundPowerSaver: BackgroundPowerSaver? = null

    override fun onCreate() {
        super.onCreate()
        backgroundPowerSaver = BackgroundPowerSaver(this);
        initNotificationChannels()
    }

    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                getString(R.string.default_notification_channel_id),
                getString(R.string.default_notification_channel_title),
                getString(R.string.default_notification_channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            createNotificationChannel(
                getString(R.string.app_bg_service_notification_channel_id),
                getString(R.string.app_bg_service_notification_channel_title),
                getString(R.string.app_bg_service_notification_channel_description),
                NotificationManager.IMPORTANCE_LOW
            )
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelTitle: String,
        channelDescription: String,
        importance: Int
    ) {
        val channel = NotificationChannel(channelId, channelTitle, importance)
        // Configure the notification channel.
        channel.description = channelDescription
        if (importance == NotificationManager.IMPORTANCE_LOW) {
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setSound(null, null)
        }
        val notificationService =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationService.createNotificationChannel(channel)
    }
}