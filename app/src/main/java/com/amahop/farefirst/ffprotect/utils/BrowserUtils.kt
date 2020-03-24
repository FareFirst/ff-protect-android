package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.amahop.farefirst.ffprotect.R

object BrowserUtils {
    private const val TAG = "BrowserUtils"
    private const val CHROME_PACKAGE_NAME = "com.android.chrome"

    private fun openExternalBrowser(
        context: Context,
        url: String
    ): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            return true
        }
        return false
    }

    /**
     * This function launches the url is ChromeTab
     *
     * @param context
     * @param url
     * @return true if launched or false if not launched
     */
    private fun openInChromeTab(
        context: Context,
        url: String?
    ): Boolean? {
        return try {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryMedium))
            builder.setShowTitle(true)
            builder.enableUrlBarHiding()
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.intent.setPackage(BrowserUtils.CHROME_PACKAGE_NAME)
            customTabsIntent.launchUrl(context, Uri.parse(url))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openInChromeTabOrExternalBrowser(
        context: Context,
        url: String
    ) {
        if (!openInChromeTab(context, url)!!) {
            if (!openExternalBrowser(context, url)) {
                LogManager.e(TAG, "openInChromeTabOrExternalBrowser failed")
            }
        }
    }
}