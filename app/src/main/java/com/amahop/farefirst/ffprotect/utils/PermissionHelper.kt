package com.amahop.farefirst.ffprotect.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.amahop.farefirst.ffprotect.BaseActivity
import com.amahop.farefirst.ffprotect.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionHelper {

    fun isLocationPermissionGranted(context: Context): Boolean {
        val loc = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val loc2 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return loc == PackageManager.PERMISSION_GRANTED && loc2 == PackageManager.PERMISSION_GRANTED
    }

    fun askLocationPermission(
        activity: BaseActivity,
        listener: (isSuccess: Boolean, errorMessage: String?) -> Unit
    ) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            listener(true, null)
                        }
                    } ?: kotlin.run {
                        listener(false, activity.getString(R.string.unknown_error))
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                listener(false, it.name)
            }
            .check()
    }

}