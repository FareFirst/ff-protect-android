package com.amahop.farefirst.ffprotect

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.altbeacon.beacon.*
import java.util.*

class HomeActivity : AppCompatActivity(), View.OnClickListener, BeaconConsumer {

    var beaconTransmitter: BeaconTransmitter? = null

    private var beaconManager: BeaconManager? = null

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupViews()
        BeaconParser.ALTBEACON_LAYOUT
    }

    private fun setupMonitor() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager?.bind(this);
    }

    private fun setupViews() {
        btnSignOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.btnSignOut -> onClickSignOut()
        }
    }

    private fun onClickSignOut() {
        btnSignOut.isEnabled = false
        btnSignOut.setText(R.string.please_wait)
        AuthManger.requestSignOut(this) {
            btnSignOut?.isEnabled = true
            btnSignOut?.setText(R.string.sign_out)
            Toast.makeText(
                this,
                R.string.failed_to_sign_out,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startAsBeacon() {
        val uuid =
            UUID.nameUUIDFromBytes(AuthManger.getCurrentUser()!!.uid.toByteArray(Charsets.UTF_8))
                .toString()
        Log.e(TAG, uuid)
        val beacon = Beacon.Builder()
            .setId1(uuid)
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(listOf(0L))
            .build()
        BeaconParser.ALTBEACON_LAYOUT
        val beaconParser = BeaconParser()
            .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT)
        beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter?.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i(TAG, "Advertisement start succeeded.")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        startAsBeacon()
        setupMonitor()
    }

    override fun onStop() {
        super.onStop()
        beaconManager?.unbind(this);
        beaconTransmitter?.stopAdvertising()
    }

    override fun onBeaconServiceConnect() {
        beaconManager?.removeAllRangeNotifiers()
        beaconManager?.addRangeNotifier { beacons, r ->
            for (beacon in beacons) {
                Log.e(
                    TAG,
                    "address: ${beacon.bluetoothAddress}, id1: ${beacon.id1}, id1: ${beacon.id2}, id1: ${beacon.id3}, distance: ${beacon.distance}"
                )
            }
        }

        try {
            beaconManager?.startRangingBeaconsInRegion(
                Region(
                    AuthManger.getCurrentUser()!!.uid,
                    null,
                    null,
                    null
                )
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}
