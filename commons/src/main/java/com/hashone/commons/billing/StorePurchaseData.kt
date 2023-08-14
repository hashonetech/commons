package com.hashone.commons.billing

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

open class StorePurchaseData(private val parentActivity: Context) {
    private var pref: SharedPreferences? = null
    private val mAppKey: String = "purchase_response_${
        parentActivity.packageName.replace("\\.".toRegex(), "_").lowercase(Locale.getDefault())
    }"

    fun getGson(): Gson {
        return GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
            .disableHtmlEscaping().generateNonExecutableJson().setLenient().setPrettyPrinting()
            .create()
    }

    private fun getString(key: String): String? {
        pref = parentActivity.getSharedPreferences(
            mAppKey, Context.MODE_PRIVATE
        )
        return pref!!.getString(key, "")
    }

    fun setString(key: String, value: String) {
        pref = parentActivity.getSharedPreferences(
            mAppKey, Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPurchaseData(key: String): PurchaseData? {
        val responseString = getString(key)
        return if (!responseString.isNullOrEmpty()) {
            try {
                getGson().fromJson(responseString, PurchaseData::class.java)
            } catch (e: Exception) {
                null
            }

        } else null
    }

    open fun savePurchaseData(key: String, purchaseData: PurchaseData) {
        pref = parentActivity.getSharedPreferences(
            mAppKey, Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putString(key, getGson().toJson(purchaseData))
        editor.apply()
    }
    fun setBoolean(key: String, value: Boolean) {
            pref = parentActivity.getSharedPreferences(
                mAppKey, Context.MODE_PRIVATE
            )
            val editor = pref!!.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }


        fun getBoolean(key: String): Boolean {
            pref = parentActivity.getSharedPreferences(
                mAppKey, Context.MODE_PRIVATE
            )
            return pref!!.getBoolean(key, false)
        }

    fun setPremiumPurchase(value: Boolean) {
        pref = parentActivity.getSharedPreferences(
            mAppKey, Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putBoolean(PurchaseManager.IS_PREMIUM_PURCHASED, value)
        editor.apply()
    }


    fun isPremiumPurchased(): Boolean {
        pref = parentActivity.getSharedPreferences(
            mAppKey, Context.MODE_PRIVATE
        )
        return pref!!.getBoolean(PurchaseManager.IS_PREMIUM_PURCHASED, false)
    }

    fun clearPurchaseData() {
        pref = parentActivity.getSharedPreferences(mAppKey, Context.MODE_PRIVATE)
        val editor = pref!!.edit()
        editor.clear()
        editor.apply()
    }
}