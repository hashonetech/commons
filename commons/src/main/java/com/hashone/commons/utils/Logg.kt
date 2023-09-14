package com.hashone.commons.utils

import android.util.Log
import com.hashone.commons.BuildConfig

object Logg {

    fun d(TAG: String, msg: String) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.d(TAG, msg)
    }

    fun d(TAG: String, msg: String, tr:Throwable) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.d(TAG, msg,tr)
    }

    fun e(TAG: String, msg: String) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.e(TAG, msg)
    }

    fun e(TAG: String?, msg: String?, tr:Throwable?) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.e(TAG, msg,tr)
    }
    fun i(TAG: String, msg: String) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.i(TAG, msg)
    }
    fun v(TAG: String, msg: String) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.v(TAG, msg)
    }
    fun w(TAG: String, msg: String) {
        if (BuildConfig.BUILD_TYPE == "debug") Log.w(TAG, msg)
    }

}