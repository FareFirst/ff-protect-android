package com.amahop.farefirst.ffprotect.tracker

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

class TrackerWorker(private val context: Context, params: WorkerParameters) :
    ListenableWorker(context, params) {

    companion object {
        const val TAG = "TrackerWorker"
    }

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            try {
                Log.d(TAG, "STARTED")
                val trackerManager = TrackerManager(this.context)

                trackerManager.start()

                val handler = Handler()
                handler.postDelayed(Runnable {
                    trackerManager.stop()
                    Log.d(TAG, "FINISHED")
                    completer.set(Result.success())
                }, TimeUnit.MINUTES.toMillis(15))
            } catch (err: Throwable) {
                Log.e(TAG, "FAILED", err)
                completer.set(Result.failure())
            }
        }
    }
}