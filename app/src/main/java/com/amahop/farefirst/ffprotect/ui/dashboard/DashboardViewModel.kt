package com.amahop.farefirst.ffprotect.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amahop.farefirst.ffprotect.ui.dashboard.repositories.DashboardDataProvider
import com.amahop.farefirst.ffprotect.ui.dashboard.repositories.pojos.GovMessage
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

    val isGovMessageFetchLoading = MutableLiveData(true)
    val isGovMessageFetchFailed = MutableLiveData(false)
    val govMessage: MutableLiveData<GovMessage?> = MutableLiveData(null)

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
        fetchGovMessage()
        isLoading.postValue(false)
    }

    fun fetchGovMessage() {
        isGovMessageFetchLoading.postValue(true)
        isGovMessageFetchFailed.postValue(false)
        DashboardDataProvider.fetchGovMessage {
            if (it != null) {
                govMessage.postValue(it)
                isGovMessageFetchFailed.postValue(false)
            } else {
                isGovMessageFetchFailed.postValue(true)
                govMessage.postValue(null)
            }
            isGovMessageFetchLoading.postValue(false)
        }
    }
}