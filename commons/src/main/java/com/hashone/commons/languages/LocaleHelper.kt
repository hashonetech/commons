package com.hashone.commons.languages

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language.Common"

    fun onAttach(context: Context?): Context? {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun setLocale(context: Context?, language: String?): Context? {
        persist(context, language)

        if (context != null) {
            val locale = Locale(language)
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