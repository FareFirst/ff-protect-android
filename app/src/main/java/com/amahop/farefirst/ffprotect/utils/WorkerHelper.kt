package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import androidx.work.*
import com.amahop.farefirst.ffprotect.sync.SyncWorker
import com.amahop.farefirst.ffprotect.tracker.TrackerWorker
import java.util.concurrent.TimeUnit

object WorkerHelper {

    fun scheduleAllPeriodicWorkers(context: Context) {
        scheduleTrackerWorker(
            context
        )
        scheduleSyncWorker(
            context
        )
    }

    fun cancelAllPeriodicWorkers(context: Context) {
        cancelTrackerWorker(
            context
        )
        cancelSyncWorker(
            context
        )
    }

    private fun scheduleTrackerWorker(context: Context) {
        val trackerWorkRequest = PeriodicWorkRequestBuilder<TrackerWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                TrackerWorker.TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                trackerWorkRequest
            )
    }

    private fun cancelTrackerWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(TrackerWorker.TAG)
    }

    private fun scheduleSyncWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            3,
            TimeUnit.HOURS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                SyncWorker.TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
    }

    private fun cancelSyncWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SyncWorker.TAG)
    }
}