package com.amahop.farefirst.ffprotect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
import com.amahop.farefirst.ffprotect.utils.WorkerHelper
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.android.gms.tasks.Task

class SplashScreenActivity : AppCompatActivity() {
    private var isActivityDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        updateAndroidSecurityProvider()
        makeGooglePlayServiceAvailable().addOnCompleteListener {
            setupRemoteConfig()
        }
    }

    private fun setupRemoteConfig() {
        RemoteConfigManager.init {
            if (isActivityDestroyed) return@init
            if (RemoteConfigManager.isAppBlocked()) {
                WorkerHelper.cancelAllPeriodicWorkers(this)
                Toast.makeText(this, R.string.app_blocked_message, Toast.LENGTH_LONG).show()
                return@init
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun makeGooglePlayServiceAvailable(): Task<Void> {
        return GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
    }

    private fun updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GoogleApiAvailability.getInstance().showErrorNotification(this, e.connectionStatusCode)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e("SecurityException", "Google Play Services not available.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
    }
}
