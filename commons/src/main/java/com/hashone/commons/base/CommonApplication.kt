package com.hashone.commons.base

import android.annotation.SuppressLint
import androidx.multidex.MultiDexApplication
import com.hashone.commons.utils.StoreUserData

open class CommonApplication : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mInstance: CommonApplication
    }

    val mStoreUserData: StoreUserData by lazy {
        StoreUserData(mInstance)
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}