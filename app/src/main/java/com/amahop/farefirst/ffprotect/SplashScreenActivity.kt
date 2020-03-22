package com.amahop.farefirst.ffprotect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amahop.farefirst.ffprotect.utils.RemoteConfigManager
import com.amahop.farefirst.ffprotect.utils.WorkerHelper

class SplashScreenActivity : AppCompatActivity() {
    private var isActivityDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setupRemoteConfig()
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

    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
    }
}
