package com.amahop.farefirst.ffprotect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.amahop.farefirst.ffprotect.tracker.TrackerManager
import com.amahop.farefirst.ffprotect.utils.AppBarConfigurer
import com.amahop.farefirst.ffprotect.utils.AuthManger
import com.amahop.farefirst.ffprotect.utils.PermissionHelper
import com.amahop.farefirst.ffprotect.utils.WorkerHelper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.view_app_bar.*

class HomeActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TAG = "HomeActivity"

        fun handleShowHomeActivity(activity: BaseActivity) {
            var clazz: Class<*> = HomeActivity::class.java
            if (AuthManger.isSignedIn()) {
                if (!PermissionHelper.isLocationPermissionGranted(activity)) {
                    clazz = PermissionActivity::class.java
                }
            } else {
                clazz = MainActivity::class.java
            }

            val intent = Intent(activity, clazz)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    private var trackerManager: TrackerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupViews()
        setupTracker()
    }

    private fun setupTracker() {
        trackerManager = TrackerManager(this)
        trackerManager?.start(TAG, true)

        WorkerHelper.scheduleAllPeriodicWorkers(this)
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        configureAppBar()
        btnSignOut.setOnClickListener(this)
    }

    private fun configureAppBar() {
        supportActionBar?.let { actionBar ->
            toolbarBg?.let { tBg ->
                AppBarConfigurer.initialize(this, actionBar, tBg)
                    .setLogo(R.drawable.ic_ff_protect_wrting_shield_logo)
                    .apply()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                Toast.makeText(this, "Show settings", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.about -> {
                Toast.makeText(this, "Show about", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        trackerManager?.stop(TAG)
        super.onDestroy()
    }
}
