package com.amahop.farefirst.ffprotect.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothHelper

class DashboardViewModel : ViewModel() {

    val isBluetoothOn = MutableLiveData<Boolean>(BluetoothHelper.isBluetoothEnabled())

    fun refreshBluetoothStatus() {
        isBluetoothOn.postValue(BluetoothHelper.isBluetoothEnabled())
    }
}