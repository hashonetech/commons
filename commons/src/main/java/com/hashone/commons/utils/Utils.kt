package com.hashone.commons.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.content.res.Resources
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(builder.emailData.email))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, builder.emailData.subject)

    val currentTime: String = SimpleDateFormat("HH:mm:ss a", Locale.getDefault()).format(Date())

    var body = message
    body += "\n\n"
    if (builder.exportToFile) {
        var dataString = ""
        dataString += "======Do not delete this======" + "\n\n"
        if (builder.appData.name.isNotEmpty())
            dataString += "Name: ${builder.appData.name}\n"
        if (builder.appData.mPackage.isNotEmpty())
            dataString += "Package: ${builder.appData.mPackage}\n"
        if (builder.appData.appVersionData.code.isNotEmpty())
            dataString += "Version: ${builder.appData.appVersionData.name} (${builder.appData.appVersionData.code})\n"
        if (builder.appData.languageData.code.isNotEmpty()) {
            dataString += "Language: ${builder.appData.languageData.code} (${builder.appData.languageData.name})\n"
        }
        dataString += "-\n"
        dataString += "Device: " + Build.BRAND + " " + Build.MODEL + "\n"
        dataString += "OS: " + Build.VERSION.SDK_INT + " (${Build.VERSION.RELEASE})" + "\n"
        dataString += "Free Memory: " + mi.availMem + "\n"
        dataString += "Resolution: $width*$height\n"
        dataString += "-\n"
        if (builder.appData.token.isNotEmpty())
            dataString += "User ID: ${builder.appData.token}\n"
        if (builder.appData.customerNumber.isNotEmpty())
            dataString += "Customer No.: ${builder.appData.customerNumber}\n"
        if (builder.purchases.isPremium) {
            if (builder.purchases.title.isNotEmpty())
                dataString += "Purchase: ${builder.purchases.title}\n"
            if (builder.purchases.orderId.isNotEmpty()) {
                dataString += "Order ID: ${builder.purchases.orderId}\n"
            }
        }
        if (builder.appData.countryData.code.isNotEmpty()) {
            dataString += if (builder.appData.countryData.name.isNotEmpty()) {
                "Country: " + builder.appData.countryData.code.uppercase() + " (${builder.appData.countryData.name})" + "\n"
            } else {
                "Country: " + builder.appData.countryData.code.uppercase() + "\n"
            }
        } else if (builder.appData.countryData.name.isNotEmpty()) {
            dataString += "Country: " + builder.appData.countryData.name + "\n"
        }
        dataString += "Time: $currentTime\n"
        dataString += "-"
        if (builder.extraContents.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            builder.extraContents.forEach {
//                if (builder.extraContents.size > 1)
                    stringBuilder.append("\n")
                stringBuilder.append("${it.key}: ${it.value}")
            }
            dataString += "${stringBuilder.toString()}\n"
        }
        dataString += "\n"

        val dataFile = writeTextInFile(context = context, dataString)
        val dataUri = FileProvider.getUriForFile(context, "${builder.appData.mPackage}.provider",dataFile)
        fileUris.add(dataUri)
    } else {
        body += "======Do not delete this======" + "\n\n"
        if (builder.appData.name.isNotEmpty())
            body += "Name: ${builder.appData.name}\n"
        if (builder.appData.mPackage.isNotEmpty())
            body += "Package: ${builder.appData.mPackage}\n"
        if (builder.appData.appVersionData.code.isNotEmpty())
            body += "Version: ${builder.appData.appVersionData.name} (${builder.appData.appVersionData.code})\n"
        if (builder.appData.languageData.code.isNotEmpty()) {
            body += "Language: ${builder.appData.languageData.code} (${builder.appData.languageData.name})\n"
        }
        body += "-\n"
        body += "Device: " + Build.BRAND + " " + Build.MODEL + "\n"
        body += "OS: " + Build.VERSION.SDK_INT + " (${Build.VERSION.RELEASE})" + "\n"
        body += "Free Memory: " + mi.availMem + "\n"
        body += "Resolution: $width*$height\n"
        body += "-\n"
        if (builder.appData.token.isNotEmpty())
            body += "User ID: ${builder.appData.token}\n"
        if (builder.appData.customerNumber.isNotEmpty())
            body += "Customer No.: ${builder.appData.customerNumber}\n"
        if (builder.purchases.isPremium) {
            if (builder.purchases.title.isNotEmpty())
                body += "Purchase: ${builder.purchases.title}\n"
            if (builder.purchases.orderId.isNotEmpty()) {
                body += "Order ID: ${builder.purchases.orderId}\n"
            }
        }
        if (builder.appData.countryData.code.isNotEmpty()) {
            body += if (builder.appData.countryData.name.isNotEmpty()) {
                "Country: " + builder.appData.countryData.code.uppercase() + " (${builder.appData.countryData.name})" + "\n"
            } else {
                "Country: " + builder.appData.countryData.code.uppercase() + "\n"
            }
        } else if (builder.appData.countryData.name.isNotEmpty()) {
            body += "Country: " + builder.appData.countryData.name + "\n"
        }
        body += "Time: $currentTime\n"
        body += "-"
        if (builder.extraContents.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            builder.extraContents.forEach {
                    stringBuilder.append("\n")
                stringBuilder.append("${it.key}: ${it.value}")
            }
            body += "${stringBuilder.toString()}\n"
        }
        body += "\n"
    }

    emailIntent.putExtra(Intent.EXTRA_TEXT, body)
    if (fileUris.size > 0) {
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
    }
    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(
            Intent.createChooser(
                //TODO: Language translation require
                emailIntent,
                context.getString(R.string.commons_email_title)
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
                    builder.appData.name,
                    emailPackageName,
                    resolveInfo,
                    selectionType,
                    builder.emailData.subject,
                    builder.emailData.email,
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
                //TODO: Language translation require
                context.getString(R.string.commons_email_title))
                .apply {
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