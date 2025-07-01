package com.coded.capstone.managers

import android.content.Context
import androidx.core.content.edit

object GeofencePreferenceManager {
    private const val PREF_NAME = "geofence_prefs"
    private const val KEY_ENABLED = "geofencing_enabled"

    fun setGeofencingEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_ENABLED, enabled)
            }
    }

    fun isGeofencingEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }
}
