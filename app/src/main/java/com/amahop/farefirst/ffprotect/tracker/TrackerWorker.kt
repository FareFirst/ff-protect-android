package com.amahop.farefirst.ffprotect.tracker

import android.content.Context
import android.os.Handler
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.amahop.farefirst.ffprotect.tracker.exceptions.SoftException
import com.amahop.farefirst.ffprotect.utils.LogManager
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

class TrackerWorker(private val context: Context, private val params: WorkerParameters) :
    ListenableWorker(context, params) {

    private var handler: Handler? = null
    private var trackerManager: TrackerManager? = null

    companion object {
        const val TAG = "TrackerWorker"
    }

    private fun getRequestTag(): String {
        return "$TAG-${params.id}"
    }

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            LogManager.d(TAG, "STARTED - ${params.id}")
            RemoteConfigManager.init {
                try {
                    trackerManager = TrackerManager(this.context)
                    trackerManager?.start(getRequestTag(), false)

                    handler = Handler()
                    handler?.postDelayed(Runnable {
                        trackerManager?.stop(getRequestTag())
                        LogManager.d(TAG, "FINISHED - ${params.id}")
                        completer.set(Result.success())
                    }, TimeUnit.MINUTES.toMillis(20))

                } catch (err: Throwable) {
                    if (err is SoftException) {
                        LogManager.d(TAG, "SKIPPED - ${params.id} - ${err.message}")
                        completer.set(Result.success())
                        return@init
                    }
                    LogManager.e(TAG, "FAILED - ${params.id}", err)
                    completer.set(Result.failure())
                }
            }

            return@getFuture completer
        }
    }

    override fun onStopped() {
        super.onStopped()
        handler?.removeCallbacksAndMessages(null)
        trackerManager?.stop(getRequestTag())
        LogManager.d(TAG, "STOPPED BY SYSTEM")
    }
}