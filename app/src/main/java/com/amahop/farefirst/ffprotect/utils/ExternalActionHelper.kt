package com.amahop.farefirst.ffprotect.utils

import android.content.Context
import android.content.Intent
import com.amahop.farefirst.ffprotect.R

object ExternalActionHelper {
    fun shareApp(context: Context) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            String.format(
                context.getString(R.string.share_string),
                context.getString(R.string.app_name),
                RemoteConfigManager.getAppShareUrl()
            )
        )
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }
}