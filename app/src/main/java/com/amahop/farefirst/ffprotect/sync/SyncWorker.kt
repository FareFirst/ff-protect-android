package com.amahop.farefirst.ffprotect.sync

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.amahop.farefirst.ffprotect.tracker.TrackerWorker
import com.amahop.farefirst.ffprotect.utils.LogManager
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
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
            LogManager.d(TAG, "STARTED - ${params.id}")
            RemoteConfigManager.init {
                try {
                    SyncManger(this.context).sync { status ->
                        when (status) {
                            ResultStatus.SUCCESS -> {
                                LogManager.d(TAG, "FINISHED - ${params.id}")
                                completer.set(Result.success())
                            }
                            ResultStatus.RETRY -> {
                                LogManager.d(TAG, "RETRY - ${params.id}")
                                completer.set(Result.retry())
                            }
                            else -> {
                                LogManager.e(TAG, "FAILED - ${params.id}")
                                completer.set(Result.failure())
                            }
                        }
                    }
                } catch (err: Throwable) {
                    LogManager.e(TAG, "FAILED - ${params.id}", err)
                    completer.set(Result.failure())
                }
            }

            return@getFuture completer
        }
    }

    override fun onStopped() {
        super.onStopped()
        LogManager.d(TrackerWorker.TAG, "STOPPED BY SYSTEM")
    }
}