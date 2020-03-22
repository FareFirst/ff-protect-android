package com.amahop.farefirst.ffprotect.tracker

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.amahop.farefirst.ffprotect.AuthManger
import com.amahop.farefirst.ffprotect.db.DBProvider
import com.amahop.farefirst.ffprotect.tracker.db.Tracker
import com.amahop.farefirst.ffprotect.tracker.exceptions.BluetoothNotEnabledException
import com.amahop.farefirst.ffprotect.utils.BluetoothHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region

class TrackerManager(private val context: Context) : BeaconConsumer {
    companion object {
        const val TAG = "TrackerManager"
    }

    private var beaconManager: BeaconManager? = null
    private var savedTrackerDistanceMap: HashMap<String, Double> = HashMap()

    fun start(tag: String, isForegroundRequest: Boolean) {
        handleBluetoothRequiredNotification(this.context, isForegroundRequest)
        if (!BluetoothHelper.isBluetoothEnabled()) {
            throw BluetoothNotEnabledException()
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
            handleBeaconsFound(beacons)
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

    @Synchronized
    private fun handleBeaconsFound(beacons: Collection<Beacon>) {
        val trackers: ArrayList<Tracker> = ArrayList()
        for (beacon in beacons) {
            Log.d(
                TAG,
                "address: ${beacon.bluetoothAddress}, id1: ${beacon.id1}, id1: ${beacon.id2}, id1: ${beacon.id3}, distance: ${beacon.distance}"
            )

            beacon.id1?.let {
                val t = Tracker(
                    trackerUuid = it.toString(),
                    bluetoothAddress = beacon.bluetoothAddress,
                    bluetoothName = beacon.bluetoothName,
                    distance = beacon.distance,
                    rssi = beacon.rssi
                )

                val existingDistance = savedTrackerDistanceMap[t.trackerUuid]
                val currentDistance = t.distance ?: Double.MAX_VALUE

                if (existingDistance == null || (existingDistance > 2 && currentDistance < 2)) {
                    trackers.add(t)
                    savedTrackerDistanceMap[t.trackerUuid] = currentDistance
                }
            }
        }

        GlobalScope.launch() {
            DBProvider.getDB(context).trackerDao().insertAll(trackers)
        }
    }
}