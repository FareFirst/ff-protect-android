package com.amahop.farefirst.ffprotect.utils.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.amahop.farefirst.ffprotect.utils.LogManager

class BluetoothStatusChangeObserver(
    private val context: Context,
    private val listener: () -> Unit
) : LifecycleObserver {
    
    companion object {
        const val TAG = "BluetoothStatusChangeObserver"
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                listener()
            }
        }
    }

    fun registerLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        LogManager.d(
            TAG,
            "onStart"
        )
        listener()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(mReceiver, filter)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        context.unregisterReceiver(mReceiver)
        LogManager.d(
            TAG,
            "onStop"
        )
    }
}