package com.hashone.commons.languages

import android.content.Context
import com.hashone.commons.R
import com.hashone.commons.extensions.getLocaleContext
import java.io.Serializable

class LanguageItem : Serializable {

    var languageName: String = ""
    var languageCode: String = ""
    var languageOriginalName: String = ""

    private var translatedBy: String = ""

    var isChecked: Boolean = false

    private var namesList = ArrayList<String>()
    private var codesList = ArrayList<String>()
    private var originalNameList = ArrayList<String>()

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
        languageOriginalName: String,
        isChecked: Boolean
    ) {
        this.languageName = languageName
        this.languageCode = languageCode
        this.languageOriginalName = languageOriginalName
        this.isChecked = isChecked
    }

    fun getLanguages(context: Context): ArrayList<LanguageItem> {
        val stringsList = ArrayList<LanguageItem>()

        namesList =
            getLocaleContext().resources.getStringArray(R.array.language_names)
                .toCollection(ArrayList())
        codesList =
            getLocaleContext().resources.getStringArray(R.array.language_codes)
                .toCollection(ArrayList())
        originalNameList =
            getLocaleContext().resources.getStringArray(R.array.language_original_names)
                .toCollection(ArrayList())

        for (i in namesList.indices) {
            stringsList.add(LanguageItem(namesList[i], codesList[i], originalNameList[i], false))
        }

        return stringsList
    }
}
