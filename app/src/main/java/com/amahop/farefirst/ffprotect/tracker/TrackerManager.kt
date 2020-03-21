package com.amahop.farefirst.ffprotect.tracker

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.amahop.farefirst.ffprotect.AuthManger
import org.altbeacon.beacon.*
import java.util.*


class TrackerManager(private val context: Context) : BeaconConsumer {
    companion object {
        const val TAG = "TrackerManager"
    }

    private var beaconTransmitter: BeaconTransmitter? = null
    private var beaconManager: BeaconManager? = null

    fun start() {
        startAsBeacon()
        setupMonitoring()
    }

    fun stop() {
        beaconManager?.unbind(this)
        beaconTransmitter?.stopAdvertising()
    }

    private fun startAsBeacon() {
        val uuid =
            UUID.nameUUIDFromBytes(AuthManger.getCurrentUser()!!.uid.toByteArray(Charsets.UTF_8))
                .toString()

        Log.d(TAG, "Beacon uuid : $uuid")

        val beacon = Beacon.Builder()
            .setId1(uuid)
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(listOf(0L))
            .build()

        val beaconParser = BeaconParser()
            .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT)

        beaconTransmitter = BeaconTransmitter(this.context, beaconParser)
        beaconTransmitter?.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i(TAG, "Advertisement start succeeded.")
            }
        })
    }

    private fun setupMonitoring() {
        beaconManager = BeaconManager.getInstanceForApplication(this.context)
        beaconManager?.enableForegroundServiceScanning(
            getTrackerRunningNotification(this.context),
            TRACKER_RUNNING_NOTIFICATION_ID
        )
        beaconManager?.setEnableScheduledScanJobs(false)
        beaconManager?.backgroundBetweenScanPeriod = 30000
        beaconManager?.backgroundScanPeriod = 5000

        beaconManager?.bind(this)
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

        beaconManager?.startRangingBeaconsInRegion(
            Region(
                AuthManger.getCurrentUser()!!.uid,
                null,
                null,
                null
            )
        )
    }
}