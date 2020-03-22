package com.amahop.farefirst.ffprotect

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.amahop.farefirst.ffprotect.tracker.TrackerWorker
import java.util.concurrent.TimeUnit

object WorkerHelper {

    fun scheduleAllPeriodicWorkers(context: Context) {
        scheduleTrackerWorker(context)
    }

    fun cancelAllPeriodicWorkers(context: Context) {
        cancelTrackerWorker(context)
    }

    fun scheduleTrackerWorker(context: Context) {
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

    fun cancelTrackerWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(TrackerWorker.TAG)
    }
}