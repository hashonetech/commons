package com.hashone.commons.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hashone.commons.R
import com.hashone.commons.contactus.ContactUs
import com.hashone.commons.databinding.SnackbarBinding
import com.hashone.commons.extensions.getEmailIntent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun checkClickTime(): Boolean {
    return if (((SystemClock.elapsedRealtime() - mLastClickTime) >= 600)) {
        mLastClickTime = SystemClock.elapsedRealtime()
        true
    } else {
        false
    }
}

fun showSnackBar(activity: Context, view: View, content: String) {
    try {
        val snackView = View.inflate(activity, R.layout.snackbar, null)
        val binding = SnackbarBinding.bind(snackView)
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        (snackbar.view as ViewGroup).removeAllViews()
        (snackbar.view as ViewGroup).addView(binding.root)
        snackbar.view.setPadding(8, 8, 8, 8)
        snackbar.view.elevation = 0f
        snackbar.setBackgroundTint(
            ContextCompat.getColor(
                activity, android.R.color.transparent
            )
        )
        binding.tvTitle.text = content
        snackbar.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun sendContactEmail(
    context: Context,
    selectionType: String,
    message: String,
    fileUris: ArrayList<Uri>,
    builder: ContactUs.Builder
) {
    val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display: Display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    val width: Int = metrics.widthPixels
    val height: Int = metrics.heightPixels

    val mi = ActivityManager.MemoryInfo()
    val activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)

    val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
    emailIntent.type = "text/plain"
    emailIntent.setPackage("com.google.android.gm")
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(builder.feedbackEmail))
    emailIntent.putExtra(
        Intent.EXTRA_SUBJECT, if (selectionType.isNotEmpty()) {
            "${builder.appName}($selectionType)"
        } else {
            builder.appName
        }
    )

    val currentTime: String = SimpleDateFormat("HH:mm:ss a", Locale.getDefault()).format(Date())

    var body = message
    body += "\n\n"
    body += "======Do not delete this======" + "\n"
    body += "App name: ${builder.appName}\n"
    body += "App Version : ${builder.versionName}\n"
    body += "Brand : " + Build.BRAND + "\n"
    body += "Manufacturer : " + Build.MANUFACTURER + "\n"
    body += "Model : " + Build.MODEL + "\n"
    body += "Android Version : " + Build.VERSION.RELEASE + "\n"
    body += "SDK : " + Build.VERSION.SDK_INT + "\n"
    body += "Free Memory : " + mi.availMem + "\n"
    body += "Screen Resolution : $width*$height\n"
    body += "Time : $currentTime\n"
    body += "Package Name: ${builder.packageName}\n"
    body += "Country Code: " + builder.countryCode + "\n"
    if (builder.isPremium) {
        body += "Purchase : ${builder.purchasedTitle}\n"
        if (builder.orderId.isNotEmpty()) {
            body += "Order ID : ${builder.orderId}\n"
        }
    }
    body += "User ID: ${builder.androidDeviceToken}\n"
    if (builder.customerNumber.isNotEmpty())
        body += "Customer No.: ${builder.customerNumber}\n"

    emailIntent.putExtra(Intent.EXTRA_TEXT, body)
    if (fileUris.size > 0) {
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
    }
    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(
            Intent.createChooser(
                emailIntent, builder.emailTitle.ifEmpty { context.getString(R.string.email_title) }
            )
        )
    } catch (e: ActivityNotFoundException) {

        val packageManager = context.packageManager
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"

        val resolveInfos = packageManager.queryIntentActivities(sendIntent, 0)
        val intentList = ArrayList<LabeledIntent>()

        for (i in 0 until resolveInfos.size step 1) {
            val resolveInfo = resolveInfos[i]
            val emailPackageName = resolveInfo.activityInfo.packageName
            if (emailPackageName.contains("android.email")) {
                emailIntent.setPackage(emailPackageName)
            } else if (emailPackageName.contains("android.gm")) {
                val intent = getEmailIntent(
                    builder.appName,
                    emailPackageName,
                    resolveInfo,
                    selectionType,
                    builder.feedbackEmail,
                    body,
                    fileUris
                )
                intentList.add(
                    LabeledIntent(
                        intent,
                        emailPackageName,
                        resolveInfo.loadLabel(packageManager),
                        resolveInfo.icon
                    )
                )
            }
        }
        context.startActivity(
            Intent.createChooser(
                emailIntent,
                builder.emailTitle.ifEmpty { context.getString(R.string.email_title) }).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
            })
    }
}

fun dpToPx(dp: Float): Float {
    return (dp * Resources.getSystem().displayMetrics.density)
}

fun openKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun getScreenWidth(context: Context): Int {
    val displayMetrics = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun getScreenHeight(context: Context): Int {
    val displayMetrics = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}