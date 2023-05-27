package com.hashone.commons.extensions

import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun getGson(): Gson {
    return GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
        .disableHtmlEscaping().generateNonExecutableJson().setLenient().setPrettyPrinting()
        .create()
}