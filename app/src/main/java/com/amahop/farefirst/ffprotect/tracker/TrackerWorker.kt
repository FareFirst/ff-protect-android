package com.amahop.farefirst.ffprotect.tracker

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

class TrackerWorker(private val context: Context, private val params: WorkerParameters) :
    ListenableWorker(context, params) {

    private val handler: Handler? = null
    private val trackerManager: TrackerManager? = null

    companion object {
        const val TAG = "TrackerWorker"
    }

    private fun getRequestTag(): String {
        return "$TAG-${params.id}"
    }

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            try {
                Log.d(TAG, "STARTED")
                val trackerManager = TrackerManager(this.context)

                trackerManager.start(getRequestTag(), false)

                handler?.postDelayed(Runnable {
                    trackerManager.stop(getRequestTag())
                    Log.d(TAG, "FINISHED")
                    completer.set(Result.success())
                }, TimeUnit.MINUTES.toMillis(20))
            } catch (err: Throwable) {
                Log.e(TAG, "FAILED", err)
                completer.set(Result.failure())
            }

            return@getFuture completer
        }
    }

    override fun onStopped() {
        super.onStopped()
        handler?.removeCallbacksAndMessages(null)
        trackerManager?.stop(getRequestTag())
        Log.d(TAG, "STOPPED BY SYSTEM")
    }
}