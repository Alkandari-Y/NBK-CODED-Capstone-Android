package com.coded.capstone.managers

import android.content.Context
import androidx.core.content.edit

object BlePreferenceManager {
    private const val PREF_NAME = "ble_prefs"
    private const val KEY_BLE_ENABLED = "ble_enabled"

    fun setBleEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_BLE_ENABLED, enabled) }
    }

    fun isBleEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BLE_ENABLED, false)
    }
}