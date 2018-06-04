package com.ryooku.mylittlebrick.utils

import android.content.Context
import android.preference.PreferenceManager
import com.ryooku.mylittlebrick.application.AppConfig

class PreferenceHelper(private val context: Context) {

    companion object {
        const val PREF_DEFAULT_URL = "defUrl"
    }


    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    private fun clearPreferences() {
        preferences.edit().clear().apply()
    }

    fun getDefaultUrl(): String {
        return preferences.getString(PREF_DEFAULT_URL, AppConfig.DEFAULT_HOST)
    }

    fun setDefaultUrl(url: String) {
        preferences.edit().putString(PREF_DEFAULT_URL, url).apply()
    }

}