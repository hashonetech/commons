package com.hashone.commons.webview

import android.app.Activity
import android.content.Intent
import android.net.Uri

class WebViewFallback : CustomTabActivityHelper.CustomTabFallback {
    override fun openUri(activity: Activity, uri: Uri, title: String) {
        activity.startActivity(
            Intent(activity, WebViewActivity::class.java)
                .putExtra("url", uri.toString())
                .putExtra("title", title)
        )
    }
}