package com.amahop.farefirst.ffprotect.sync

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
import com.crashlytics.android.Crashlytics
import com.google.common.util.concurrent.ListenableFuture

class SyncWorker(private val context: Context, private val params: WorkerParameters) :
    ListenableWorker(context, params) {

    companion object {
        const val TAG = "SyncWorker"

        enum class ResultStatus {
            SUCCESS, FAILED, RETRY
        }
    }

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            try {
                Log.d(TAG, "STARTED - ${params.id}")
                RemoteConfigManager.init {
                    SyncManger(this.context).sync { status ->
                        when (status) {
                            ResultStatus.SUCCESS -> {
                                Log.d(TAG, "FINISHED - ${params.id}")
                                completer.set(Result.success())
                            }
                            ResultStatus.RETRY -> {
                                Log.d(TAG, "RETRY - ${params.id}")
                                completer.set(Result.retry())
                            }
                            else -> {
                                Log.e(TAG, "FAILED - ${params.id}")
                                completer.set(Result.failure())
                            }
                        }
                    }
                }
            } catch (err: Throwable) {
                Log.e(TAG, "FAILED - ${params.id}", err)
                Crashlytics.logException(err)
                completer.set(Result.failure())
            }

            return@getFuture completer
        }
    }
}