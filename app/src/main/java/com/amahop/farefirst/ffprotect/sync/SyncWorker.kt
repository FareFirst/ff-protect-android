package com.amahop.farefirst.ffprotect.sync

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.crashlytics.android.Crashlytics
import com.google.common.util.concurrent.ListenableFuture

class SyncWorker(private val context: Context, params: WorkerParameters) :
    ListenableWorker(context, params) {

    companion object {
        const val TAG = "SyncWorker"
    }

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            try {
                Log.d(TAG, "STARTED")
                SyncManger(this.context).sync { isSuccess ->
                    if (isSuccess) {
                        Log.d(TAG, "FINISHED")
                        completer.set(Result.success())
                    } else {
                        Log.e(TAG, "FAILED")
                        completer.set(Result.failure())
                    }
                }
            } catch (err: Throwable) {
                Log.e(TAG, "FAILED", err)
                Crashlytics.logException(err)
                completer.set(Result.failure())
            }

            return@getFuture completer
        }
    }
}