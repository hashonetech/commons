package com.hashone.commons.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.hashone.commons.languages.LanguageItem
import com.hashone.commons.languages.LocaleHelper
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME
import com.hashone.commons.utils.StoreUserData
import java.util.Locale

class CommonApplication : MultiDexApplication() {

    init {
        System.loadLibrary("native-lib")
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mInstance: CommonApplication
    }

    var mContext: Context? = null
    lateinit var mStoreUserData: StoreUserData

    fun setLocaleContext(context: Context) {
        this.mContext = context
    }

    override fun attachBaseContext(base: Context?) {
        if (base != null) {
            val languagesList = LanguageItem().getLanguages(base)
            mStoreUserData = StoreUserData(base)

            mStoreUserData.apply {
                if (getString(DEFAULT_LANGUAGE)!!.isEmpty()) {
                    val defaultLanguage =
                        Locale.getDefault().language.lowercase(Locale.getDefault()).trim()
                    var isContain = false
                    for (i in 0 until languagesList.size) {
                        if (languagesList[i].languageCode.equals(
                                defaultLanguage,
                                ignoreCase = true
                            )
                        ) {
                            isContain = true
                        }
                    }
                    if (isContain) {
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE,
                            Locale.getDefault().language.lowercase(Locale.getDefault()).trim()
                        )
                        for (i in 0 until languagesList.size) {
                            if (mStoreUserData.getString(DEFAULT_LANGUAGE)!!.equals(
                                    languagesList[i].languageCode,
                                    ignoreCase = true
                                )
                            ) {
                                mStoreUserData.setString(
                                    DEFAULT_LANGUAGE_NAME,
                                    languagesList[i].languageName
                                )
                            }
                        }
                    } else {
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE,
                            languagesList[0].languageCode
                        )
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE_NAME,
                            languagesList[0].languageName
                        )
                    }
                }
            }
            super.attachBaseContext(
                LocaleHelper.setLocale(
                    base,
                    mStoreUserData.getString(DEFAULT_LANGUAGE)
                )
            )
            MultiDex.install(base)
        } else {
            super.attachBaseContext(null)
        }
    }
}