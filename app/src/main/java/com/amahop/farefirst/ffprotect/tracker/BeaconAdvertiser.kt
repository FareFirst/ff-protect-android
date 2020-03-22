package com.amahop.farefirst.ffprotect.tracker

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.util.Log
import com.amahop.farefirst.ffprotect.utils.AuthManger
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*
import kotlin.collections.HashMap

object BeaconAdvertiser {
    private const val TAG = "BeaconAdvertiser"

    private var beaconTransmitter: BeaconTransmitter? = null

    var hashMap: HashMap<String, Boolean> = HashMap<String, Boolean>()

    @Synchronized
    fun start(context: Context, tag: String) {
        if (hashMap[tag] != null) {
            Log.d(TAG, "Already running for tag")
            return
        }

        val currentUser = AuthManger.getCurrentUser()

        if (currentUser == null) {
            Log.d(TAG, "User not signed in so can't start advertising. Ignoring $tag")
            return
        }

        if (hashMap.keys.size == 0) {
            startAdvertising(context, currentUser.uid)
            Log.d(TAG, "starting advertising")
        }

        hashMap[tag] = true
        Log.i(TAG, "Added $tag")
    }

    @Synchronized
    fun stop(tag: String) {
        if (hashMap[tag] != null) {
            Log.d(TAG, "Unknown TAG for stop")
            return
        }

        hashMap.remove(tag)
        Log.i(TAG, "Removed $tag")

        if (hashMap.keys.size == 0) {
            beaconTransmitter?.stopAdvertising()
            beaconTransmitter = null
            Log.d(TAG, "Stopping advertising")
        }
    }

    private fun startAdvertising(context: Context, uid: String) {
        val uuid =
            UUID.nameUUIDFromBytes(uid.toByteArray(Charsets.UTF_8))
                .toString()

        Log.d(TrackerManager.TAG, "Beacon uuid : $uuid")

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

        beaconTransmitter = BeaconTransmitter(context, beaconParser)
        beaconTransmitter?.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e(TrackerManager.TAG, "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i(TrackerManager.TAG, "Advertisement start succeeded.")
            }
        })
    }
}