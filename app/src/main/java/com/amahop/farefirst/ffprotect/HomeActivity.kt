package com.amahop.farefirst.ffprotect

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.amahop.farefirst.ffprotect.tracker.TrackerWorker
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupViews()
        setupTracker()
    }

    private fun setupTracker() {
        val trackerWorkRequest = PeriodicWorkRequestBuilder<TrackerWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                TrackerWorker.TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                trackerWorkRequest
            )
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
}
