package com.amahop.farefirst.ffprotect.tracker

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import com.amahop.farefirst.ffprotect.db.DBProvider
import com.amahop.farefirst.ffprotect.tracker.db.Tracker
import com.amahop.farefirst.ffprotect.tracker.exceptions.AppBlockedException
import com.amahop.farefirst.ffprotect.tracker.exceptions.BluetoothNotEnabledException
import com.amahop.farefirst.ffprotect.tracker.exceptions.LocationPermissionNotGrantedException
import com.amahop.farefirst.ffprotect.tracker.exceptions.TrackerBlockedException
import com.amahop.farefirst.ffprotect.utils.*
import com.google.firebase.auth.FirebaseUser
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
        if (RemoteConfigManager.isAppBlocked()) {
            WorkerHelper.cancelAllPeriodicWorkers(context)
            throw AppBlockedException()
        }

        if (!Settings.isTrackerOn()) {
            WorkerHelper.cancelAllPeriodicWorkers(context)
            throw TrackerBlockedException()
        }

        if (!PermissionHelper.isLocationPermissionGranted(this.context)) {
            throw LocationPermissionNotGrantedException()
        }

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
                LogManager.d(TAG, "Consumer already bound")
                return
            }

            it.enableForegroundServiceScanning(
                getTrackerRunningNotification(this.context),
                TRACKER_RUNNING_NOTIFICATION_ID
            )
            it.foregroundBetweenScanPeriod = RemoteConfigManager.getForegroundBetweenScanPeriod()
            it.foregroundScanPeriod = RemoteConfigManager.getForegroundScanPeriod()
            it.backgroundBetweenScanPeriod = RemoteConfigManager.getBackgroundBetweenScanPeriod()
            it.backgroundScanPeriod = RemoteConfigManager.getBackgroundScanPeriod()
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

        LocationHelper.requestLocation(this.context) { location ->
            AuthManger.getCurrentUser()?.let { currentUser ->
                beaconManager?.addRangeNotifier { beacons, _ ->
                    handleBeaconsFound(currentUser, beacons, location)
                }

                beaconManager?.startRangingBeaconsInRegion(
                    Region(
                        currentUser.uid,
                        null,
                        null,
                        null
                    )
                )
            } ?: kotlin.run {
                LogManager.d(TAG, "User not signed in so can't startRangingBeaconsInRegion")
            }
        }
    }

    @Synchronized
    private fun handleBeaconsFound(
        currentUser: FirebaseUser,
        beacons: Collection<Beacon>,
        location: Location?
    ) {
        val trackers: ArrayList<Tracker> = ArrayList()
        for (beacon in beacons) {
            LogManager.d(
                TAG,
                "address: ${beacon.bluetoothAddress}, id1: ${beacon.id1}, id1: ${beacon.id2}, id1: ${beacon.id3}, distance: ${beacon.distance}"
            )

            beacon.id1?.let {
                val t = Tracker(
                    clientUserUid = currentUser.uid,
                    trackerUuid = it.toString(),
                    bluetoothAddress = beacon.bluetoothAddress,
                    bluetoothName = beacon.bluetoothName,
                    distance = beacon.distance,
                    rssi = beacon.rssi,
                    latitude = location?.latitude,
                    longitude = location?.longitude
                )

                val existingDistance = savedTrackerDistanceMap[t.trackerUuid]
                val currentDistance = t.distance ?: Double.MAX_VALUE

                if (existingDistance == null || (existingDistance > 2 && currentDistance < 2)) {
                    trackers.add(t)
                    savedTrackerDistanceMap[t.trackerUuid] = currentDistance
                }
            }
        }

        if (trackers.isNotEmpty()) {
            GlobalScope.launch {
                DBProvider.getDB(context).trackerDao().insertAll(trackers)
            }
        }
    }
}