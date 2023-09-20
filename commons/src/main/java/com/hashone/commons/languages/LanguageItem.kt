package com.hashone.commons.languages

import java.io.Serializable

class LanguageItem : Serializable {

    var languageName: String = ""
    var languageCode: String = ""
    var countryCode: String = ""
    var languageOriginalName: String = ""

    private var translatedBy: String = ""

    var isChecked: Boolean = false

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

    constructor(
        languageName: String,
        languageCode: String,
        languageOriginalName: String,
        countryCode: String,
        isChecked: Boolean
    ) {
        this.languageName = languageName
        this.languageCode = languageCode
        this.languageOriginalName = languageOriginalName
        this.countryCode = countryCode
        this.isChecked = isChecked
    }
}
