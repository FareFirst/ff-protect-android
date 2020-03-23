package com.amahop.farefirst.ffprotect

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceFragmentCompat
import com.amahop.farefirst.ffprotect.utils.AppBarConfigurer
import com.amahop.farefirst.ffprotect.utils.LogManager
import com.amahop.farefirst.ffprotect.utils.Settings
import com.amahop.farefirst.ffprotect.utils.WorkerHelper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.view_app_bar.*

class SettingsActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val TAG = "SettingsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        configureAppBar()
    }

    private fun configureAppBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let { actionBar ->
            toolbarBg?.let { tBg ->
                AppBarConfigurer.initialize(this, actionBar, tBg)
                    .enableHomeAsUp()
                    .apply()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        LogManager.d(TAG, "onSharedPreferenceChanged => $key")
        key?.let {
            if (it == Settings.PREF_KEY_IS_TRACKER_ON) {
                if (Settings.isTrackerOn()) {
                    WorkerHelper.scheduleAllPeriodicWorkers(this)
                } else {
                    WorkerHelper.cancelAllPeriodicWorkers(this)
                }
            }
        } ?: kotlin.run {
            LogManager.e(TAG, "Key is null")
        }
    }

    override fun onStart() {
        super.onStart()
        Settings.getSP().registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        Settings.getSP().unregisterOnSharedPreferenceChangeListener(this)
    }
}