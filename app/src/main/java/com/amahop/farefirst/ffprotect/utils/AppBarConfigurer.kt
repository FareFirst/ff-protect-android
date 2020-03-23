package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import com.amahop.farefirst.ffprotect.R

class AppBarConfigurer private constructor(
    val context: Context,
    private val actionBar: ActionBar,
    private val appBarView: View
) {

    private var title: String? = null
    @StringRes
    private var titleResId: Int? = null
    private var subTitle: String? = null
    @StringRes
    private var subTitleResId: Int? = null
    @DrawableRes
    private var logoResId: Int? = null
    @DrawableRes
    private var customIconId: Int? = null
    private var isCloseEnabled = false
    private var isTransparent = false
    private var isHomeAsUpEnabled = false
    private var isDisableTitle = false

    companion object {
        private const val INVALID_STATUS_BAR_HEIGHT = -1f
        private var statusBarHeight: Float = INVALID_STATUS_BAR_HEIGHT

        fun initialize(context: Context, actionBar: ActionBar, appBarView: View): AppBarConfigurer {
            return AppBarConfigurer(context, actionBar, appBarView)
        }

        // Make sure to call this method when the app begins
        fun fetchStatusBarHeight(context: Context, view: View, listener: ((Float) -> Unit)?) {
            //Returning default status bar height as there is no need of bothering about updated
            // status bar height due to display cutout in landscape orientation
            if (SystemUtils.isLandscapeOrientation(context)) {
                listener?.let { it(SystemUtils.getStatusBarHeight(context)) }
                return
            }
            if (statusBarHeight == INVALID_STATUS_BAR_HEIGHT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    view.setOnApplyWindowInsetsListener { v, insets ->
                        insets?.let {
                            statusBarHeight = it.systemWindowInsetTop.toFloat()
                            listener?.let { it(statusBarHeight) }
                        }
                        v.setOnApplyWindowInsetsListener(null)
                        insets
                    }
                } else {
                    statusBarHeight = SystemUtils.getStatusBarHeight(context)
                    listener?.let { it(statusBarHeight) }
                }
            } else {
                listener?.let { it(statusBarHeight) }
            }
        }

        fun setStatusBarPadding(context: Context, view: View) {
            // Set padding
            fetchStatusBarHeight(context, view) { height ->
                view.setPadding(0, height.toInt(), 0, 0)
            }
        }
    }

    fun setTitle(@StringRes titleResId: Int): AppBarConfigurer {
        this.titleResId = titleResId
        return this
    }

    fun setTitle(title: String): AppBarConfigurer {
        this.title = title
        return this
    }

    fun setSubTitle(@StringRes subTitleResId: Int): AppBarConfigurer {
        this.subTitleResId = subTitleResId
        return this
    }

    fun setSubTitle(subTitle: String): AppBarConfigurer {
        this.subTitle = subTitle
        return this
    }

    fun setLogo(@DrawableRes logoResId: Int): AppBarConfigurer {
        this.logoResId = logoResId
        return this
    }

    fun enableClose(): AppBarConfigurer {
        isCloseEnabled = true
        return this
    }

    fun enableTransparency(): AppBarConfigurer {
        isTransparent = true
        return this
    }

    fun enableHomeAsUp(): AppBarConfigurer {
        isHomeAsUpEnabled = true
        return this
    }

    fun disableTitle(): AppBarConfigurer {
        isDisableTitle = true
        return this
    }

    fun setCustomIcon(@DrawableRes customIconId: Int): AppBarConfigurer {
        this.customIconId = customIconId
        return this
    }

    fun apply() {
        // Logo or title
        if (logoResId != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            logoResId?.let { actionBar.setLogo(it) }
        } else if (titleResId != null || subTitleResId != null || title != null || subTitle != null) {
            actionBar.setLogo(null)
            actionBar.setDisplayShowTitleEnabled(true)

            title?.let { actionBar.title = it }
            titleResId?.let { actionBar.setTitle(it) }

            subTitle?.let { actionBar.subtitle = it }
            subTitleResId?.let { actionBar.setSubtitle(it) }

        }

        //If title not needed
        if (isDisableTitle) {
            actionBar.setDisplayShowTitleEnabled(false)
        }

        // Back or Close
        if (isHomeAsUpEnabled) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            customIconId?.let {
                actionBar.setHomeAsUpIndicator(it)
            } ?: run {
                actionBar.setHomeAsUpIndicator(if (isCloseEnabled) R.drawable.ic_close_white_24dp else R.drawable.ic_arrow_back_white_24dp)
            }
        }

        // Transparent or gradient bg
        appBarView.setBackgroundResource(if (isTransparent) android.R.color.transparent else R.drawable.farefirst_widget_gradient_drawable)

        // Set padding
        setStatusBarPadding(context, appBarView)
    }
}