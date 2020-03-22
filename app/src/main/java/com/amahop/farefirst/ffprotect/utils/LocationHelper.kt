package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.location.Location
import android.util.Log
import com.amahop.farefirst.ffprotect.sync.SyncManger
import com.google.android.gms.location.LocationServices

object LocationHelper {
    fun requestLocation(context: Context, listener: (location: Location?) -> Unit) {
        if (!Settings.isAllowedToTrackLocation()) {
            Log.d(SyncManger.TAG, "Location tracking not allowed")
            listener(null)
            return
        }

        if (!PermissionHelper.isLocationPermissionGranted()) {
            Log.d(SyncManger.TAG, "Location permission not granted")
            listener(null)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            listener(it)
        }
    }
}