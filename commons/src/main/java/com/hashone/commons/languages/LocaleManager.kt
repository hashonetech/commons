package com.hashone.commons.languages

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hashone.commons.base.CommonApplication
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_COUNTY_CODE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME
import java.util.Locale

object LocaleManager {

    val mLanguagesList = ArrayList<LanguageItem>()

    fun prepareLanguageList(languageList: ArrayList<LanguageItem>) {
        mLanguagesList.clear()
        mLanguagesList.addAll(languageList)
    }

    fun verifyCurrentAppLocale(context: Context) {
        checkMigrationIsAlreadyDone(context)
        checkIfLanguageExist()
        updateAppPreference()
    }

    /*--------------------------------------------------------------------------------------------*/
    private fun checkMigrationIsAlreadyDone(context: Context) {
        // Check if the migration has already been done or not
        if (getString(context, FIRST_TIME_MIGRATION) != STATUS_DONE) {
            // Fetch the selected language from wherever it was stored. In this case it’s SharedPref
            // In this case let’s assume that it was stored in a key named SELECTED_LANGUAGE
            getString(context, SELECTED_LANGUAGE)?.let {
                // Set this locale using the AndroidX library that will handle the storage itself
                val localeList = LocaleListCompat.forLanguageTags(it)
                AppCompatDelegate.setApplicationLocales(localeList)

                // Set the migration flag to ensure that this is executed only once
                putString(context, FIRST_TIME_MIGRATION, STATUS_DONE)
            }
        }
    }

    private fun checkIfLanguageExist() {
        if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            var selectedLanguageTag = AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()
            val languageCodeList = ArrayList<String>()

            mLanguagesList.forEach {
                languageCodeList.add(it.languageCode)
            }
            if (!languageCodeList.contains(selectedLanguageTag)) {
                var isLanguageCodeExist = false
                languageCodeList.forEachIndexed { index, item ->
                    selectedLanguageTag?.let { languageCode ->
                        if (languageCode.equals(item, ignoreCase = true)) {
                            selectedLanguageTag = item
                            isLanguageCodeExist = true
                        } else if (languageCode.startsWith(item, ignoreCase = true)) {
                            val systemLanguageCodeData =
                                selectedLanguageTag?.substringBeforeLast("-")
                            if (systemLanguageCodeData.equals(item, true)) {
                                selectedLanguageTag = item
                                isLanguageCodeExist = true
                            }
                        }
                    }
                }
                if (isLanguageCodeExist) {
                    val localeList = LocaleListCompat.forLanguageTags(selectedLanguageTag)
                    AppCompatDelegate.setApplicationLocales(localeList)
                } else {
                    val localeList = LocaleListCompat.forLanguageTags("en")
                    AppCompatDelegate.setApplicationLocales(localeList)
                }
            }
        } else {
            val localeData = getAppLocale()
            val localeList =
                LocaleListCompat.forLanguageTags(if (localeData != null) localeData.toLanguageTag() else "en")
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    fun isLocaleContains(currentLocale: Locale?): Boolean {
        var isContains = false
        if (currentLocale != null) {
            mLanguagesList.forEach {
                if (currentLocale.toLanguageTag().equals(it.languageCode, ignoreCase = true)) {
                    isContains = true
                } else if (currentLocale.toLanguageTag().startsWith(it.languageCode, ignoreCase = true)) {
                    val systemLanguageCodeData = currentLocale.toLanguageTag().substringBeforeLast("-")
                    if (systemLanguageCodeData.equals(it.languageCode, true))
                        isContains = true
                }
            }
        }
        return isContains
    }

    fun updateSelection() {
        val currentLocale = getAppLocale()
        if (currentLocale != null) {
            mLanguagesList.forEach { languageItem ->
                if (currentLocale.toLanguageTag().equals(languageItem.languageCode, ignoreCase = true)) {
                    languageItem.isChecked = true
                } else if (currentLocale.toLanguageTag().startsWith(languageItem.languageCode, ignoreCase = true)) {
                    val systemLanguageCodeData = currentLocale.toLanguageTag().substringBeforeLast("-")
                    languageItem.isChecked =
                        systemLanguageCodeData.equals(languageItem.languageCode, true)
                } else {
                    languageItem.isChecked = false
                }
            }
        } else {
            mLanguagesList.forEach { languageItem ->
                languageItem.isChecked = "en".equals(languageItem.languageCode, ignoreCase = true)
            }
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    //TODO: App Locale
    fun setAppLocale(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        // Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getAppLocale(): Locale? {
        // Fetching the current application locale using the AndroidX support Library
        // Call this to get the selected locale and display it in your App
        return if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            // Fetches the current Application Locale from the list
            AppCompatDelegate.getApplicationLocales()[0]
        } else {
            // Fetches the default System Locale
            Locale.getDefault()
        }
    }

    /*--------------------------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------------------------*/
    //TODO: Preference management

    // Specify the constants to be used in the below code snippets
    // Constants for SharedPreference File
    const val PREFERENCE_NAME = "shared_preference"
    const val PREFERENCE_MODE = Context.MODE_PRIVATE

    // Constants for SharedPreference Keys
    const val FIRST_TIME_MIGRATION = "first_time_migration"
    const val SELECTED_LANGUAGE = "selected_language"

    // Constants for SharedPreference Values
    const val STATUS_DONE = "status_done"

    // Utility method to put a string in a SharedPreference
    private fun putString(context: Context, key: String, value: String) {
        val editor = context.getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Utility method to get a string from a SharedPreference
    private fun getString(context: Context, key: String): String? {
        val preference = context.getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE)
        return preference.getString(key, null)
    }

    private fun updateAppPreference() {
        if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            val selectedLanguageTag = AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()
            if (mLanguagesList.isEmpty()) {
                mLanguagesList.add(
                    LanguageItem(
                        languageName = "English",
                        languageCode = "en",
                        languageOriginalName = "",
                        countryCode = "IN",
                        isChecked = true
                    )
                )
            }
            mLanguagesList.forEach {
                if (it.languageCode == selectedLanguageTag) {
                    CommonApplication.mInstance.mStoreUserData.setString(
                        DEFAULT_LANGUAGE,
                        it.languageCode
                    )
                    CommonApplication.mInstance.mStoreUserData.setString(
                        DEFAULT_LANGUAGE_NAME,
                        it.languageName
                    )
                    CommonApplication.mInstance.mStoreUserData.setString(
                        DEFAULT_LANGUAGE_COUNTY_CODE, it.countryCode
                    )
                }
            }
        }
    }
    /*--------------------------------------------------------------------------------------------*/

}