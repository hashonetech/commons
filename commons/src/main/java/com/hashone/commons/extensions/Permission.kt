package com.hashone.commons.extensions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Activity.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED