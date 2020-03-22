package com.amahop.farefirst.ffprotect.sync

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.crashlytics.android.Crashlytics
import com.google.common.util.concurrent.ListenableFuture

class SyncWorker(private val context: Context, private val params: WorkerParameters) :
    ListenableWorker(context, params) {

    companion object {
        const val TAG = "SyncWorker"
    }

    private fun getRequestTag(): String {
        return "$TAG-${params.id}"
    }

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            try {
                Log.d(TAG, "STARTED")

                Log.d(TAG, "FINISHED")
                completer.set(Result.success())
            } catch (err: Throwable) {

                Log.e(TAG, "FAILED", err)
                Crashlytics.logException(err)
                completer.set(Result.failure())
            }

            return@getFuture completer
        }
    }
}