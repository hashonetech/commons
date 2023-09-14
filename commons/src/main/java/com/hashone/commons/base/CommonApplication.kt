package com.hashone.commons.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.hashone.commons.extensions.getLocaleString
import com.hashone.commons.languages.LanguageItem
import com.hashone.commons.languages.LocaleHelper
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_COUNTY_CODE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME
import com.hashone.commons.utils.StoreUserData
import java.util.Locale
import kotlin.collections.ArrayList

open class CommonApplication : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mInstance: CommonApplication
    }

    var languageList: ArrayList<LanguageItem> = ArrayList()
    var mContext: Context? = null
    lateinit var mStoreUserData: StoreUserData
//    var languageList
    fun setLocaleContext(context: Context) {
        this.mContext = context
    }

    override fun attachBaseContext(base: Context?) {
        mInstance = this
        if (base != null) {
            setLocaleContext(base)
            val languagesList = LanguageItem().getLanguages(base)
            mStoreUserData = StoreUserData(base)

            mStoreUserData.apply {
                if (getString(DEFAULT_LANGUAGE)!!.isEmpty()) {
                    var defaultLanguage =
                        Locale.getDefault().language.lowercase(Locale.getDefault()).trim()
                    var defaultLanguageName = ""
                    var defaultCountryCode = ""

                    var isContain = false
                    for (i in 0 until languagesList.size) {
                        if (languagesList[i].languageCode.equals(
                                defaultLanguage,
                                ignoreCase = true
                            )
                        ) {
                            isContain = true
                            defaultLanguageName = languagesList[i].languageName
                            defaultCountryCode = languagesList[i].countryCode
                        }
                    }
                    if (!isContain){
                        defaultLanguage = Locale.getDefault().language.lowercase(Locale.getDefault()).trim() + if (Locale.getDefault().language.lowercase(Locale.getDefault()).trim() == Locale.getDefault().country.lowercase(Locale.getDefault()).trim()) "" else ("-"+Locale.getDefault().country.lowercase(Locale.getDefault()).trim())
                            Locale.getDefault().language.lowercase(Locale.getDefault()).trim()
                        languagesList.forEach {
                            if (it.languageCode.equals(
                                    defaultLanguage,
                                    ignoreCase = true
                                )
                            ) {
                                isContain = true
                                defaultLanguageName = it.languageName
                                defaultCountryCode = it.countryCode

                            }
                        }
                    }
                    if (isContain) {
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE,
                            defaultLanguage
                        )
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE_NAME,
                            defaultLanguageName
                        )
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE_COUNTY_CODE,
                            defaultCountryCode
                        )

                    } else {
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE,
                            languagesList[0].languageCode
                        )
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE_NAME,
                            languagesList[0].languageName
                        )
                        mStoreUserData.setString(
                            DEFAULT_LANGUAGE_COUNTY_CODE,
                            languagesList[0].countryCode
                        )
                    }
                }
            }
            super.attachBaseContext(
                LocaleHelper.setLocale(
                    base,
                    mStoreUserData.getString(DEFAULT_LANGUAGE),
                    mStoreUserData.getString(DEFAULT_LANGUAGE_COUNTY_CODE)
                )
            )
            MultiDex.install(base)
        } else {
            setLocaleContext(applicationContext)
            super.attachBaseContext(null)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        languageList = LanguageItem().getLanguages(this)
    }
}