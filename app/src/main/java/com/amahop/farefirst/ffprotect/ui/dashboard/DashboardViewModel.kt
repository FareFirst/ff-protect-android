package com.amahop.farefirst.ffprotect.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amahop.farefirst.ffprotect.utils.AuthManger
import com.amahop.farefirst.ffprotect.utils.Settings
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothHelper

class DashboardViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>(false)
    val isBluetoothOn = MutableLiveData<Boolean>(BluetoothHelper.isBluetoothEnabled())
    val isTrackerRunning = MutableLiveData<Boolean>(Settings.isTrackerOn())
    val lastSyncedAt = MutableLiveData<Long>(Settings.getLastSyncedAt())
    val phoneNumber = MutableLiveData(AuthManger.getPhoneNumber())
    val isTrackerOn = MutableLiveData(Settings.isTrackerOn())

    fun refreshBluetoothStatus() {
        isBluetoothOn.postValue(BluetoothHelper.isBluetoothEnabled())
    }

    fun refreshTrackRunningStatus() {
        isTrackerRunning.postValue(Settings.isTrackerOn())
    }

    fun refreshIsTrackerOn() {
        isTrackerOn.postValue(Settings.isTrackerOn())
    }

    fun refreshLastSyncedAt() {
        lastSyncedAt.postValue(Settings.getLastSyncedAt())
    }

    fun refreshPhoneNumber() {
        phoneNumber.postValue(AuthManger.getPhoneNumber())
    }

    fun refreshAll() {
        isLoading.postValue(true)
        refreshBluetoothStatus()
        refreshTrackRunningStatus()
        refreshLastSyncedAt()
        refreshPhoneNumber()
        refreshIsTrackerOn()
        isLoading.postValue(false)
    }
}