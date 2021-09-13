package com.reinkyatto.webjens.prefs

import android.content.Context
import android.content.SharedPreferences
import com.reinkyatto.webjens.App

class SharedPrefs {
    private val key = "KEY_SHARED_PREFS"
    private val keyToken = "token"

    private fun getSharedPrefs(): SharedPreferences {
        return App.applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        with(getSharedPrefs().edit()) {
            putString(keyToken, token)
            apply()
        }
    }

    fun getToken(): String {
        return getSharedPrefs().getString(keyToken, "") ?: ""
    }
}