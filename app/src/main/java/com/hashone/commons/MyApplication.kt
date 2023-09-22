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
                LanguageItem("bahasa Indonesia", "in-ID", "Indonesian", false),
                LanguageItem("বাংলা", "bn-BD", "Bangla", false),
                LanguageItem("Deutsche", "de-DE", "German", false),
                LanguageItem("English", "en-US", "", true),//TODO:No SubTitle
                LanguageItem("Española", "es-ES", "Spanish", false),
                LanguageItem("Filipino", "fil-PH", "Filipino", false),
                LanguageItem("français", "fr-FR", "French", false),
                LanguageItem("Italiano", "it-IT", "Italian", false),
                LanguageItem("português", "pt-PT", "Portuguese", false),
                LanguageItem("pусский", "ru-RU", "Russian", false),
                LanguageItem("Türkçe", "tr-TR", "Turkish", false),
                LanguageItem("yкраїнський", "uk-UA", "Ukrainian", false),
                LanguageItem("Chinese Simplified", "zh-Hans-CN", "Chinese Simplified", false),
                LanguageItem(
                    "Chinese (Hong Kong) Simplified",
                    "zh-Hans-HK",
                    "Chinese (Hong Kong) Simplified",
                    false
                ),LanguageItem(
                    "Chinese (Macao) Simplified",
                    "zh-Hans-MO",
                    "Chinese (Macao) Simplified",
                    false
                ),LanguageItem(
                    "Chinese (Singapore) Simplified",
                    "zh-Hans-SG",
                    "Chinese (Singapore) Simplified",
                    false
                ),
                LanguageItem(
                    "Chinese (Taiwan) Traditional",
                    "zh-Hant-TW",
                    "Chinese (Taiwan) Traditional",
                    false
                ),
                LanguageItem(
                    "Chinese (Hong Kong) Traditional",
                    "zh-Hant-HK",
                    "Chinese (Hong Kong) Traditional",
                    false
                ),
            )
        )
    }
}