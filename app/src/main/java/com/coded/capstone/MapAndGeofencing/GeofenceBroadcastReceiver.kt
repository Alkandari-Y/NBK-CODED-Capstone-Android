package com.coded.capstone.MapAndGeofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofenceStatusCodes
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import com.coded.capstone.managers.GeofencePreferenceManager
import com.coded.capstone.managers.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")

        // Handle device boot
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            Log.d(TAG, "Device booted or app updated, starting geofence service")
            // Check permissions before starting service
            val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasBackgroundLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasForegroundServiceLocation = if (android.os.Build.VERSION.SDK_INT >= 34) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
            } else true
            if (hasFineLocation && hasBackgroundLocation && hasForegroundServiceLocation) {
                val serviceIntent = Intent(context, GeofenceService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } else {
                Log.w(TAG, "Not starting GeofenceService: required location permissions not granted.")
            }
            return
        }

        // Handle test intents
        intent.getStringExtra("test_enter")?.let { mallId ->
            Log.d(TAG, "Received test enter notification for mall: $mallId")
            // GeofenceManager.showNotification(context, GeofenceManager.getMallName(mallId), true)
            return
        }
        intent.getStringExtra("test_exit")?.let { mallId ->
            Log.d(TAG, "Received test exit notification for mall: $mallId")
            // GeofenceManager.showNotification(context, GeofenceManager.getMallName(mallId), false)
            return
        }

        // Handle real geofence events
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = when (geofencingEvent.errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "Geofence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many geofences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
                else -> "Unknown error: ${geofencingEvent.errorCode}"
            }
            Log.e(TAG, "Geofencing error: $errorMessage")
            return
        }

        // Get the transition type
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER &&
            geofenceTransition != Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e(TAG, "Invalid geofence transition type: $geofenceTransition")
            return
        }

        // Get the geofences that triggered this event
        val triggeringGeofences = geofencingEvent.triggeringGeofences
        if (triggeringGeofences.isNullOrEmpty()) {
            Log.e(TAG, "No triggering geofences found")
            return
        }

        Log.d(TAG, "Processing ${triggeringGeofences.size} geofence events")

        // Start the service if it's not running
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasBackgroundLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasForegroundServiceLocation = if (android.os.Build.VERSION.SDK_INT >= 34) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true

        // modification to allow users to toggle geofencing on or off
        if (!GeofencePreferenceManager.isGeofencingEnabled(context)) {
            Log.d(TAG, "Geofencing disabled in preferences; not starting service on boot.")
            return
        }
        if (hasFineLocation && hasBackgroundLocation && hasForegroundServiceLocation) {
            val serviceIntent = Intent(context, GeofenceService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            Log.w(TAG, "Not starting GeofenceService: required location permissions not granted.")
        }

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER, Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val transitionType = if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) "ENTER" else "EXIT"
                triggeringGeofences.forEach { geofence ->
                    Log.d(TAG, "Geofence event: ${geofence.requestId} - $transitionType")
                    // Launch a coroutine to call the backend API
                    CoroutineScope(Dispatchers.IO).launch {
                        val userId = TokenManager.getUserIdFromSharedPref(context)
                        GeofenceApiManager.notifyBackendOfGeofenceEvent(
                            context = context,
                            geofenceId = geofence.requestId,
                            transitionType = transitionType,
                            userId = userId
                        )
                    }
                }
            }
        }
    }
} 