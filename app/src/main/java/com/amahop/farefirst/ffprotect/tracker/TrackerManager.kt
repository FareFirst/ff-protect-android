package com.amahop.farefirst.ffprotect.tracker

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.amahop.farefirst.ffprotect.AuthManger
import com.amahop.farefirst.ffprotect.utils.BluetoothHelper
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region


class TrackerManager(private val context: Context) : BeaconConsumer {
    companion object {
        const val TAG = "TrackerManager"
    }

    private var beaconManager: BeaconManager? = null

    fun start(tag: String, isForegroundRequest: Boolean) {
        handleBluetoothRequiredNotification(this.context, isForegroundRequest)
        if (!BluetoothHelper.isBluetoothEnabled()) {
            Log.w(TAG, "Not starting the JOB as bluetooth is not enabled")
            return
        }
        BeaconAdvertiser.start(this.context, tag)
        setupMonitoring()
    }

    fun stop(tag: String) {
        beaconManager?.unbind(this)
        BeaconAdvertiser.stop(tag)
    }

    private fun setupMonitoring() {
        beaconManager = BeaconManager.getInstanceForApplication(this.context)
        beaconManager?.let {
            if (it.isAnyConsumerBound) {
                Log.d(TAG, "Consumer already bound")
                return
            }

            it.enableForegroundServiceScanning(
                getTrackerRunningNotification(this.context),
                TRACKER_RUNNING_NOTIFICATION_ID
            )
            it.foregroundBetweenScanPeriod = 20000
            it.foregroundScanPeriod = 5000
            it.backgroundBetweenScanPeriod = 30000
            it.backgroundScanPeriod = 5000
            it.bind(this)
        }
    }

    override fun getApplicationContext(): Context {
        return this.context
    }

    override fun unbindService(connection: ServiceConnection) {
        this.context.unbindService(connection)
    }

    override fun bindService(service: Intent, connection: ServiceConnection, flags: Int): Boolean {
        return this.context.bindService(service, connection, flags)
    }

    override fun onBeaconServiceConnect() {
        beaconManager?.removeAllRangeNotifiers()
        beaconManager?.addRangeNotifier { beacons, _ ->
            for (beacon in beacons) {
                Log.i(
                    TAG,
                    "address: ${beacon.bluetoothAddress}, id1: ${beacon.id1}, id1: ${beacon.id2}, id1: ${beacon.id3}, distance: ${beacon.distance}"
                )
            }
        }

        AuthManger.getCurrentUser()?.let { currentUser ->
            beaconManager?.startRangingBeaconsInRegion(
                Region(
                    currentUser.uid,
                    null,
                    null,
                    null
                )
            )
        } ?: kotlin.run {
            Log.d(TAG, "User not signed in so can't startRangingBeaconsInRegion")
        }
    }
}