package com.amahop.farefirst.ffprotect.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope

class CoroutineTrackerWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
//        val jobs = (0 until 100).map {
//            async {
//                downloadSynchronously("https://www.google.com")
//            }
//        }
//
//        // awaitAll will throw an exception if a download fails, which CoroutineWorker will treat as a failure
//        jobs.awaitAll()
        Result.success()
    }
}