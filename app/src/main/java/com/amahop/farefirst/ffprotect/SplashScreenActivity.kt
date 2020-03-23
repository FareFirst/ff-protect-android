package com.amahop.farefirst.ffprotect

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.amahop.farefirst.ffprotect.utils.*
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : BaseActivity() {
    private var isActivityDestroyed = false

    companion object {
        private const val TAG = "SplashScreenActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setupStatusBarHeight()
        updateAndroidSecurityProvider()
        makeGooglePlayServiceAvailable().addOnCompleteListener {
            Handler().postDelayed({
                setup()
            }, 1000)
        }
    }

    private fun setup() {
        if (BluetoothHelper.isBluetoothAvailable()) {
            setupRemoteConfig()
        } else {
            Toast.makeText(this, R.string.bluetooth_not_available, Toast.LENGTH_LONG).show()
            finish()
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
            HomeActivity.handleShowHomeActivity(this)
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
            LogManager.e("SecurityException", "Google Play Services not available.")
        }
    }

    private fun setupStatusBarHeight() {
        AppBarConfigurer.fetchStatusBarHeight(this, statusBarBg) {
            Log.d(TAG, "setupStatusBarHeight fetched -> $it")
            statusBarBg?.layoutParams?.height = it.toInt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
    }
}
