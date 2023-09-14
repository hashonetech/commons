package com.hashone.commons.webview

import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import java.lang.ref.WeakReference

/**
 * Implementation for the CustomTabsServiceConnection that avoids leaking the
 * ServiceConnectionCallback
 */
class ServiceConnection : CustomTabsServiceConnection() {
    // A weak reference to the ServiceConnectionCallback to avoid leaking it.
    private var mConnectionCallback: WeakReference<ServiceConnectionCallback>? = null

    override fun onServiceDisconnected(name: ComponentName?) {
        if (mConnectionCallback != null) {
            val connectionCallback = mConnectionCallback!!.get()
            connectionCallback?.onServiceDisconnected()
        }
    }

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        if (mConnectionCallback != null) {
            val connectionCallback = mConnectionCallback!!.get()
            connectionCallback?.onServiceConnected(client)
        }
    }
}