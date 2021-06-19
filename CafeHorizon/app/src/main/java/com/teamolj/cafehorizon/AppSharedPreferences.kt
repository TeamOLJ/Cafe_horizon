package com.teamolj.cafehorizon

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context: Context) {
    private val PREFS_FILE_NAME = "horizon_user_prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)
    private val editor : SharedPreferences.Editor = prefs.edit()

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        editor.putString(key, str)
        editor.commit()
    }

    fun clear() {
        editor.clear()
        editor.commit()
    }
}