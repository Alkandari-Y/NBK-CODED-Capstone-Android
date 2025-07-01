package com.coded.capstone.managers

import android.content.Context
import androidx.core.content.edit

object GeofencePreferenceManager {
    private const val PREF_NAME = "geofence_prefs"
    private const val KEY_ENABLED = "geofencing_enabled"

    fun setGeofencingEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .commit()
    }

    fun isGeofencingEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun ensureDefaultDisabled(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_ENABLED)) {
            prefs.edit().putBoolean(KEY_ENABLED, false).apply()
        }
    }
}
