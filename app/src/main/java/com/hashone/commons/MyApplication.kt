package com.hashone.commons

import android.content.Context
import com.hashone.commons.base.CommonApplication
import com.hashone.commons.languages.LanguageItem

class MyApplication : CommonApplication() {

    override fun attachBaseContext(base: Context?) {
        prepareLanguageList()
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    private fun prepareLanguageList() {
        languageList.clear()
        languageList.addAll(
            arrayListOf(
                LanguageItem("bahasa Indonesia", "in", "Indonesian", false),
                LanguageItem("বাংলা", "bn", "Bengali", false),
                LanguageItem("Deutsche", "de", "German", false),
                LanguageItem("English", "en", "", false),//TODO:No SubTitle
                LanguageItem("Española", "es", "Spanish", false),
                LanguageItem("Filipino", "fil", "Filipino", false),//TODO:No SubTitle
                LanguageItem("français", "fr", "French", false),
                LanguageItem("Italiano", "it", "Italian", false),
                LanguageItem("português", "pt", "Portuguese", false),
                LanguageItem("pусский", "ru", "Russian", false),
                LanguageItem("Türkçe", "tr", "Turkish", false),
                LanguageItem("yкраїнський", "uk", "Ukrainian", false),
            )
        )
    }
}