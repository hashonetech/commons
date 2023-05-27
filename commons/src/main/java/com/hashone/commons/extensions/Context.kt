package com.hashone.commons.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.DisplayMetrics
import android.view.Display
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.hashone.commons.base.CommonApplication

/**
 * Extension method to startActivity with Animation for Context.
 */
inline fun <reified T : Activity> Context.startActivityWithAnimation(
    enterResId: Int = 0, exitResId: Int = 0
) {
    ContextCompat.startActivity(
        this,
        Intent(this, T::class.java),
        ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    )
}

/**
 * Extension method to startActivity with Animation for Context.
 */
inline fun <reified T : Activity> Context.startActivityWithAnimation(
    enterResId: Int = 0, exitResId: Int = 0, intentBody: Intent.() -> Unit
) {
    ContextCompat.startActivity(
        this,
        Intent(this, T::class.java).apply(intentBody),
        ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    )
}

/**
 * Extension method to show toast for Context.
 */
fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

/**
 * Extension method to show toast for Context.
 */
fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, textId, duration).show() }

/**
 * Extension method to Get Integer resource for Context.
 */
fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

/**
 * Extension method to Get Color for resource for Context.
 */
fun Context.getColorCode(@ColorRes id: Int) = ContextCompat.getColor(this, id)

/**
 * Extension method to Get Drawable for resource for Context.
 */
fun Context.getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.isGooglePhotosAppInstalled(packageName: String): Boolean = run {
    try {
        packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return false
    }
    true
}

fun Context.isNetworkAvailable(): Boolean = run {
    var result = false
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    result
}

fun Context.getDisplayMetrics(): DisplayMetrics? = run {
    val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val defaultDisplay = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    val defaultDisplayContext = createDisplayContext(defaultDisplay)
    defaultDisplayContext.resources.displayMetrics
}

fun Context.getScreenWidth(): Int = getDisplayMetrics()!!.widthPixels

fun Context.getScreenHeight(): Int = getDisplayMetrics()!!.heightPixels

fun getLocaleContext(): Context = CommonApplication.mInstance.mContext!!

fun getLocaleString(@StringRes id: Int) = getLocaleContext().getString(id)