package com.hashone.commons.extensions

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri

fun getEmailIntent(
    appName: String = "",
    packageName: String = "",
    resolveInfo: ResolveInfo? = null,
    selectionType: String = "",
    email: String,
    body: String,
    fileUris: ArrayList<Uri>
): Intent {
    return Intent().apply {
        if (resolveInfo != null)
            component = ComponentName(packageName, resolveInfo.activityInfo.name)
        action = Intent.ACTION_SEND_MULTIPLE
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(
            Intent.EXTRA_SUBJECT, "$appName($selectionType)"
        )
        putExtra(Intent.EXTRA_TEXT, body)
        if (fileUris.size > 0) {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

fun getMediaPickIntent(allowPhotoOnly: Boolean, allowVideoOnly: Boolean, allowBoth: Boolean): Intent {
    return Intent(Intent.ACTION_GET_CONTENT).apply {
        if (allowPhotoOnly) {
            type = "image/*"
        } else if (allowVideoOnly) {
            type = "video/*"
        } else if (allowBoth) {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        }
    }
}