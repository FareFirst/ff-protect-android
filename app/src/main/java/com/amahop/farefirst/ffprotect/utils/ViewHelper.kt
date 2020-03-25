package com.amahop.farefirst.ffprotect.utils

import android.os.Build
import android.text.Html
import android.widget.TextView

object ViewHelper {
    @Suppress("DEPRECATION")
    fun setHtmlTextToTextView(tv: TextView, htmlStr: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.text = Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_COMPACT)
        } else {
            tv.text = Html.fromHtml(htmlStr)
        }
    }
}