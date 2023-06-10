package com.hashone.commons.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken
import com.hashone.commons.extensions.getGson
import java.util.Locale

open class StoreUserData(private var parentActivity: Context) {
    var pref: SharedPreferences? = null
    private var mAppKey: String = ""

    fun setString(key: String, value: String) {
        val editor = pref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return pref!!.getString(key, "")
    }

    fun setBoolean(key: String, value: Boolean) {
        val editor = pref!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return pref!!.getBoolean(key, false)
    }

    fun setInt(key: String, value: Int) {
        val editor = pref!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int {
        return pref!!.getInt(key, -1)
    }

    fun setLong(key: String, value: Long) {
        val editor = pref!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String): Long {
        return pref!!.getLong(key, 0)
    }

    private val keyDownloadedContents = "key_downloaded_contents"
    fun updateDownloadedContentIds(contentId: Int) {
        val contentIdsList = getDownloadedContentIds()
        if (!contentIdsList.contains(contentId)) {
            contentIdsList.add(contentId)
            setString(keyDownloadedContents, getGson().toJson(contentIdsList))
        }
    }

    fun getDownloadedContentIds(): ArrayList<Int> {
        val contentIds = getString(keyDownloadedContents)
        if (!contentIds.isNullOrEmpty()) {
            return getGson().fromJson(
                contentIds,
                object : TypeToken<ArrayList<Int>>() {}.type
            )
        }
        return arrayListOf()
    }

    private val keyUnlockedContents = "key_unlocked_contents"
    fun updateUnlockedContentIds(contentId: Int) {
        val contentIdsList = getUnlockedContentIds()
        if (!contentIdsList.contains(contentId)) {
            contentIdsList.add(contentId)
            setString(keyUnlockedContents, getGson().toJson(contentIdsList))
        }
    }

    private fun getUnlockedContentIds(): ArrayList<Int> {
        val contentIds = getString(keyUnlockedContents)
        if (!contentIds.isNullOrEmpty()) {
            return getGson().fromJson(
                contentIds,
                object : TypeToken<ArrayList<Int>>() {}.type
            )
        }
        return arrayListOf()
    }

    fun isExistInUnlocked(contentId: Int): Boolean {
        return getUnlockedContentIds().contains(contentId)
    }

    fun getAllKeys(): MutableMap<String, *>? {
        return pref?.all
    }

    init {
        mAppKey = parentActivity.packageName.replace("\\.".toRegex(), "_")
            .lowercase(Locale.getDefault())
        pref = parentActivity.getSharedPreferences(
            mAppKey,
            Context.MODE_PRIVATE
        )
    }
}