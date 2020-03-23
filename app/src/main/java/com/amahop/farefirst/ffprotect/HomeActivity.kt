package com.amahop.farefirst.ffprotect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.amahop.farefirst.ffprotect.tracker.TrackerManager
import com.amahop.farefirst.ffprotect.utils.AuthManger
import com.amahop.farefirst.ffprotect.utils.PermissionHelper
import com.amahop.farefirst.ffprotect.utils.WorkerHelper
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TAG = "HomeActivity"

        fun handleShowHomeActivity(activity: BaseActivity) {
            var clazz: Class<BaseActivity> = HomeActivity::class.java as Class<BaseActivity>
            if (AuthManger.isSignedIn()) {
                if (!PermissionHelper.isLocationPermissionGranted(activity)) {
                    clazz = PermissionActivity::class.java as Class<BaseActivity>
                }
            } else {
                clazz = MainActivity::class.java as Class<BaseActivity>
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

    override fun onDestroy() {
        trackerManager?.stop(TAG)
        super.onDestroy()
    }
}
