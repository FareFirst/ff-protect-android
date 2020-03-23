package com.amahop.farefirst.ffprotect

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.amahop.farefirst.ffprotect.utils.AppBarConfigurer
import com.amahop.farefirst.ffprotect.utils.LogManager
import com.amahop.farefirst.ffprotect.utils.PermissionHelper
import kotlinx.android.synthetic.main.activity_permission.*

class PermissionActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TAG = "PermissionActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        setupStatusBarHeight()
        setupViews()
    }

    private fun setupStatusBarHeight() {
        AppBarConfigurer.fetchStatusBarHeight(this, statusBarBg) {
            statusBarBg?.layoutParams?.height = it.toInt()
        }
    }

    private fun setupViews() {
        btnGetStarted.setOnClickListener(this)
    }

    private fun onClickGetStarted() {
        PermissionHelper.askLocationPermission(this) { isSuccess, errorMessage ->
            if (isSuccess) {
                showHomeScreen()
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                LogManager.d(TAG, "Failed to get permission $errorMessage")
            }
        }
    }

    private fun showHomeScreen() {
        HomeActivity.handleShowHomeActivity(this)
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.btnGetStarted -> onClickGetStarted()
        }
    }
}
