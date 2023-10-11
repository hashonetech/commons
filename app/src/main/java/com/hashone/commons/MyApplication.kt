package com.hashone.commons

import com.hashone.commons.base.CommonApplication
import com.hashone.commons.languages.LanguageItem
import com.hashone.commons.languages.LocaleManager

class MyApplication : CommonApplication() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        //TODO: App implementation
        setupAppLocale()
    }

    //TODO: App implementation
    private fun setupAppLocale() {
        LocaleManager.prepareLanguageList(
            arrayListOf(
                LanguageItem("bahasa Indonesia", "id", "Indonesian", false),
                LanguageItem("বাংলা", "bn", "Bangla", false),
                LanguageItem("Deutsche", "de", "German", false),
                LanguageItem("English", "en", "", true),//TODO:No SubTitle
                LanguageItem("हिंदी", "hi", "Hindi", true),
                LanguageItem("Española", "es", "Spanish", false),
                LanguageItem("Filipino", "fil", "Filipino", false),
                LanguageItem("français", "fr", "French", false),
                LanguageItem("Italiano", "it", "Italian", false),
                LanguageItem("português", "pt", "Portuguese", false),
                LanguageItem("pусский", "ru", "Russian", false),
                LanguageItem("Türkçe", "tr", "Turkish", false),
                LanguageItem("yкраїнський", "uk", "Ukrainian", false),
                LanguageItem("Chinese Simplified", "zh-Hans", "Chinese Simplified", false),
                LanguageItem(
                    "Chinese (Taiwan) Traditional",
                    "zh-Hant",
                    "Chinese (Taiwan) Traditional",
                    false
                ),
                LanguageItem(
                    "Chinese (Macao) Traditional",
                    "zh-Hant-MO",
                    "Chinese (Macao) Traditional",
                    false
                ),
            )
        )
    }
}