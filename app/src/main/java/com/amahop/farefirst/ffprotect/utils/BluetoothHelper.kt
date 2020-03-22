package com.amahop.farefirst.ffprotect.utils

import android.bluetooth.BluetoothAdapter

class BluetoothHelper {
    companion object {
        fun isBluetoothAvailable(): Boolean {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter != null
        }

        fun isBluetoothEnabled(): Boolean {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter != null && bluetoothAdapter.isEnabled
        }
    }
}