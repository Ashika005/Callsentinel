package com.example.callsentinel.utils

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "call_sentinel_prefs"
        private const val KEY_PROTECTION_ACTIVE = "protection_active"
        private const val KEY_SENSITIVITY = "sensitivity_threshold"
        private const val KEY_AUTO_BLOCK = "auto_block_active"
        private const val KEY_FIRST_LAUNCH = "is_first_launch"
    }

    var isProtectionActive: Boolean
        get() = prefs.getBoolean(KEY_PROTECTION_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_PROTECTION_ACTIVE, value).apply()

    var sensitivityThreshold: Int
        get() = prefs.getInt(KEY_SENSITIVITY, 50) // Default to 50
        set(value) = prefs.edit().putInt(KEY_SENSITIVITY, value).apply()

    var isAutoBlockActive: Boolean
        get() = prefs.getBoolean(KEY_AUTO_BLOCK, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_BLOCK, value).apply()
        
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
}
