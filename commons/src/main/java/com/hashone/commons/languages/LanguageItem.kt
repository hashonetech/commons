package com.hashone.commons.languages

import android.content.Context
import com.hashone.commons.R
import com.hashone.commons.extensions.getLocaleContext
import java.io.Serializable

class LanguageItem : Serializable {

    var languageName: String = ""
    var languageCode: String = ""

    private var translatedBy: String = ""

    var isChecked: Boolean = false

    private var namesList = ArrayList<String>()
    private var codesList = ArrayList<String>()

    constructor()

    constructor(
        languageName: String,
        languageCode: String,
        translatedBy: String
    ) {
        this.languageName = languageName
        this.languageCode = languageCode
        this.translatedBy = translatedBy
    }

    constructor(
        languageName: String,
        languageCode: String,
        isChecked: Boolean
    ) {
        this.languageName = languageName
        this.languageCode = languageCode
        this.translatedBy = translatedBy
    }

    fun getLanguages(context: Context): ArrayList<LanguageItem> {
        val stringsList = ArrayList<LanguageItem>()

        namesList =
            getLocaleContext().resources.getStringArray(R.array.language_names)
                .toCollection(ArrayList())
        codesList =
            getLocaleContext().resources.getStringArray(R.array.language_codes)
                .toCollection(ArrayList())
        for (i in namesList.indices) {
            stringsList.add(LanguageItem(namesList[i], codesList[i], false))
        }

        return stringsList
    }
}
