package com.amahop.farefirst.ffprotect.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amahop.farefirst.ffprotect.utils.AuthManger
import com.amahop.farefirst.ffprotect.utils.Settings
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothHelper

class DashboardViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>(false)
    val isBluetoothOn = MutableLiveData<Boolean>(BluetoothHelper.isBluetoothEnabled())
    val isTrackerRunning = MutableLiveData<Boolean>(false)
    val lastSyncedAt = MutableLiveData<Long>(Settings.getLastSyncedAt())
    val phoneNumber = MutableLiveData(AuthManger.getPhoneNumber())
    val isTrackerOn = MutableLiveData(Settings.isTrackerOn())

    fun refreshBluetoothStatus() {
        isBluetoothOn.postValue(BluetoothHelper.isBluetoothEnabled())
    }

    fun setIsTrackerRunning(value: Boolean) {
        isTrackerRunning.postValue(value)
    }

    private fun refreshIsTrackerOn() {
        isTrackerOn.postValue(Settings.isTrackerOn())
    }

    private fun refreshLastSyncedAt() {
        lastSyncedAt.postValue(Settings.getLastSyncedAt())
    }

    private fun refreshPhoneNumber() {
        phoneNumber.postValue(AuthManger.getPhoneNumber())
    }

    fun refreshAll() {
        isLoading.postValue(true)
        refreshBluetoothStatus()
        refreshLastSyncedAt()
        refreshPhoneNumber()
        refreshIsTrackerOn()
        isLoading.postValue(false)
    }
}