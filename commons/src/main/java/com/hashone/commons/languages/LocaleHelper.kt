package com.hashone.commons.languages

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.gson.Gson
import com.hashone.commons.base.CommonApplication
import com.hashone.commons.utils.ACTION_LANGUAGE_CHANGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_COUNTY_CODE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language.Common"

    fun onAttach(context: Context?): Context? {
        val lang = getPersistedData(context, Locale.getDefault().language)
        val countyCode = getPersistedData(context, Locale.getDefault().country)
        return setLocale(context, lang, countyCode)
    }

    fun setLocale(context: Context?, language: String?): Context? {
        persist(context, language)

        if (context != null) {
            Locale.SIMPLIFIED_CHINESE
            val locale = Locale(language, "")
            Locale.setDefault(locale)
            val config = Configuration()

            config.setLocale(locale)
            context.resources.updateConfiguration(
                config, context.resources.displayMetrics
            )
        }
        return context
    }

    fun setLocale(context: Context?, language: String?, countyCode: String?): Context? {
        persist(context, language)

        if (context != null) {
            val locale = Locale(language,countyCode)
            Locale.setDefault(locale)
            val config = Configuration()

            config.setLocale(locale)
            context.resources.updateConfiguration(
                config, context.resources.displayMetrics
            )
        }
        return context
    }

    private fun getPersistedData(context: Context?, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context?, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }
}