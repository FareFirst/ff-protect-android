package com.amahop.farefirst.ffprotect.tracker

import android.os.CountDownTimer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.amahop.farefirst.ffprotect.utils.LogManager

class TrackerStatusObserver(
    private val trackerManager: TrackerManager,
    private val listener: (isRunning: Boolean) -> Unit
) : LifecycleObserver {

    companion object {
        const val TAG = "TrackerStatusObserver"
    }

    private var countDownTimer: CountDownTimer? = null

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 2000) {
            override fun onFinish() {
                start()
            }

            override fun onTick(millisUntilFinished: Long) {
                updateStatus()
            }
        }.start()
    }

    private fun stopTimer() {
        try {
            countDownTimer?.cancel()
            countDownTimer = null
        } catch (th: Throwable) {
            LogManager.e(TAG, "Error while cancelling", th)
        }
    }

    private fun updateStatus() {
        listener(trackerManager.isRunning())
    }

    fun registerLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        LogManager.d(
            TAG,
            "onResume"
        )
        updateStatus()
        startTimer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        stopTimer()
        LogManager.d(
            TAG,
            "onPause"
        )
    }
}