package com.example.winhey.utils

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtil {
    private const val PREFS_NAME = "MyAppPrefs"
    private const val KEY_APP_VERSION = "app_version"

    fun saveAppVersion(context: Context, version: String) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(KEY_APP_VERSION, version)
            apply()
        }
    }

    fun getAppVersion(context: Context): String? {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val versionName = context.packageName?.let { it1 ->
            context.packageManager
                ?.getPackageInfo(it1, 0)?.versionName
        }
        return sharedPref.getString(KEY_APP_VERSION, versionName)
    }
}
