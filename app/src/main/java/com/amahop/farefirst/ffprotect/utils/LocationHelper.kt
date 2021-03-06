package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices

object LocationHelper {
    private const val TAG = "LocationHelper"

    fun requestLocation(context: Context, listener: (location: Location?) -> Unit) {
        if (!Settings.isAllowedToTrackLocation()) {
            LogManager.d(TAG, "Location tracking not allowed")
            listener(null)
            return
        }

        if (!PermissionHelper.isLocationPermissionGranted(context)) {
            LogManager.d(TAG, "Location permission not granted")
            listener(null)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                listener(it.result)
            } else {
                listener(null)
            }
        }
    }
}