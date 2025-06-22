package com.coded.capstone.providers

import com.coded.capstone.MapAndGeofencing.GeofenceEventRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for geofence-related API calls.
 */
interface GeofenceServiceProvider {

    /**
     * Notifies the backend when a geofence event (e.g., enter or exit) occurs.
     * This call is secured and requires a valid JWT token, which is handled
     * by the TokenInterceptor.
     *
     * @param request The request body containing the geofence ID and transition type.
     * @return A Response with no body, indicating success or failure.
     */
    @POST("/api/v1/geofence/event")
    suspend fun sendGeofenceEvent(
        @Body request: GeofenceEventRequest
    ): Response<Void>
} 