package com.coded.capstone.MapAndGeofencing

import android.content.Context
import android.util.Log
import com.coded.capstone.providers.RetrofitInstance

/**
 * Manages API calls related to geofence events.
 */
object GeofenceApiManager {

    private const val TAG = "GeofenceApiManager"

    /**
     * Finds the full location details by its ID and notifies the backend when a geofence event occurs.
     *
     * This function is called from the GeofenceBroadcastReceiver. It looks up the
     * complete MallLocation object, constructs the rich request, and sends it to the backend.
     *
     * @param context The application context.
     * @param geofenceId The ID of the geofence that was triggered (e.g., "the_avenues").
     * @param transitionType The type of transition ("ENTER" or "EXIT").
     */
    suspend fun notifyBackendOfGeofenceEvent(
        context: Context,
        geofenceId: String,
        transitionType: String,
        userId: Long
    ) {
        // Find the full location details from the GeofenceManager's list
        val location = GeofenceManager.mallLocations.find { it.id == geofenceId }

        if (location == null) {
            Log.e(TAG, "Could not find location details for geofenceId: $geofenceId")
            return
        }

        try {
            // Build the rich request object with all location details
            val request = GeofenceEventRequest(
                id = location.id,
                name = location.name,
                latitude = location.location.latitude,
                longitude = location.location.longitude,
                radius = location.radius,
                type = location.type.name,
                description = location.description,
                tags = location.tags,
                transitionType = transitionType,
                userId = userId
            )

            val service = RetrofitInstance.getGeofenceServiceProvider(context)
            val response = service.sendGeofenceEvent(request)

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully notified backend of geofence event: ${location.name} - $transitionType")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to notify backend. Code: ${response.code()}, Error: $errorBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while notifying backend of geofence event: ${e.message}", e)
        }
    }
} 