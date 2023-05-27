package com.hashone.commons.extensions

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.core.content.ContextCompat

fun Activity.registerBroadCastReceiver(
    broadcastReceiver: BroadcastReceiver,
    intentFilter: IntentFilter
) {
    ContextCompat.registerReceiver(
        this,
        broadcastReceiver,
        intentFilter,
        ContextCompat.RECEIVER_NOT_EXPORTED
    )
}