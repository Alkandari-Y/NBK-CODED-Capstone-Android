package com.coded.capstone.MapAndGeofencing

/**
 * Data class representing the rich payload sent to the backend when a geofence event occurs.
 * It contains the full details of the location that was triggered, making it easier for the
 * backend to process without needing to look up location details by ID.
 *
 * @property id The unique identifier of the location (e.g., "the_avenues").
 * @property name The display name of the location (e.g., "The Avenues Mall").
 * @property latitude The geographical latitude of the location.
 * @property longitude The geographical longitude of the location.
 * @property radius The radius of the geofence in meters.
 * @property type The category or type of the location (e.g., "MALL").
 * @property description A brief description of the location.
 * @property tags A list of tags associated with the location for categorization.
 * @property transitionType The type of geofence transition, e.g., "ENTER" or "EXIT".
 */

data class GeofenceEventRequest(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val type: String,
    val description: String,
    val tags: List<String>,
    val transitionType: String,
    val userId: Long?
) 