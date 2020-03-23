package com.amahop.farefirst.ffprotect

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableUiOverStatusBar()
    }

    private fun enableUiOverStatusBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setStatusBarColor(android.R.color.transparent)
    }

    private fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = resources.getColor(color)
        }
    }
}
