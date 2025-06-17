package com.coded.capstone.MapAndGeofencing

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.coded.capstone.MainActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

private const val TAG = "GeofenceManager"

enum class LocationType {
    MALL,
    SHOPPING_CENTER,
    BUSINESS_DISTRICT,
    ALL
}

data class MallLocation(
    val id: String,
    val name: String,
    val location: LatLng,
    val radius: Float = 300f,
    val type: LocationType = LocationType.MALL,
    val description: String = "",
    val tags: List<String> = emptyList()
)

object GeofenceManager {
    val mallLocations = listOf(
        MallLocation(
            id = "the_avenues",
            name = "The Avenues Mall",
            location = LatLng(29.3041671444999, 47.93671843875713),
            radius = 4000f,
            type = LocationType.MALL,
            description = "One of Kuwait's largest shopping malls",
            tags = listOf("shopping", "entertainment", "dining")
        ),
        MallLocation(
            id = "360_mall",
            name = "360 Mall",
            location = LatLng(29.269424968695457, 47.992110292332065),
            radius = 2000f,
            type = LocationType.MALL,
            description = "Modern shopping mall with luxury brands",
            tags = listOf("luxury", "shopping", "dining")
        ),
        MallLocation(
            id = "al_hamra",
            name = "Al Hamra Mall",
            location = LatLng(29.3797166248848, 47.99344955683091),
            radius = 500f,
            type = LocationType.MALL,
            description = "Popular shopping destination",
            tags = listOf("shopping", "entertainment")
        ),
        MallLocation(
            id = "marina_mall",
            name = "Marina Mall",
            location = LatLng(29.339498339626992, 48.06573277635584),
            radius = 600f,
            type = LocationType.MALL,
            description = "Waterfront shopping mall",
            tags = listOf("shopping", "waterfront", "dining")
        ),
        MallLocation(
            id = "al_kout_mall",
            name = "Al Kout Mall",
            location = LatLng(29.07857305894003, 48.13910215677374),
            radius = 600f,
            type = LocationType.MALL,
            description = "Shopping mall in Fahaheel",
            tags = listOf("shopping", "dining")
        ),
        MallLocation(
            id = "al_assema_mall",
            name = "Al Assema Mall",
            location = LatLng(29.373999514076708, 47.98680669409609),
            radius = 600f,
            type = LocationType.MALL,
            description = "Community shopping mall",
            tags = listOf("shopping", "community")
        ),
        MallLocation(
            id = "gate_mall",
            name = "Gate Mall",
            location = LatLng(29.175163255722016, 48.09878748375732),
            radius = 600f,
            type = LocationType.MALL,
            description = "Shopping and entertainment complex",
            tags = listOf("shopping", "entertainment")
        ),
        MallLocation(
            id = "nbk_hq",
            name = "NBK Headquarters",
            location = LatLng(29.378102803691213, 47.992681977654),
            radius = 500f,
            type = LocationType.BUSINESS_DISTRICT,
            description = "National Bank of Kuwait Headquarters",
            tags = listOf("business", "banking")
        ),
        MallLocation(
            id = "al_rai_mall",
            name = "Al Rai Mall",
            location = LatLng(29.377413397552395, 47.990297991900384),
            radius = 500f,
            type = LocationType.MALL,
            description = "Shopping mall in Al Rai area",
            tags = listOf("shopping", "community")
        )
    )

    private const val CHANNEL_ID = "mall_geofence_channel"
    private const val NOTIFICATION_ID = 1
    private var isChannelCreated = false
    private var geofencingClient: GeofencingClient? = null
    private var isGeofencingActive = false

    suspend fun startGeofencing(context: Context) {
        if (!hasRequiredPermissions(context)) {
            Log.e(TAG, "Location permissions are required for geofencing")
            throw SecurityException("Location permissions are required for geofencing")
        }

        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting geofencing initialization...")
                geofencingClient = LocationServices.getGeofencingClient(context)
                
                // Create notification channel first
                createNotificationChannel(context)
                Log.d(TAG, "Notification channel created/verified")
                
                // Remove any existing geofences first
                Log.d(TAG, "Removing existing geofences...")
                try {
                    geofencingClient?.removeGeofences(getGeofencePendingIntent(context))?.await()
                    Log.d(TAG, "Existing geofences removed successfully")
                } catch (e: Exception) {
                    Log.w(TAG, "No existing geofences to remove or error removing: ${e.message}")
                }
                
                // Add new geofences
                val request = createGeofenceRequest()
                Log.d(TAG, "Adding ${request.geofences?.size} geofences")
                
                try {
                    geofencingClient?.addGeofences(request, getGeofencePendingIntent(context))?.await()
                    isGeofencingActive = true
                    Log.d(TAG, "Geofencing started successfully")
                    
                    // Print current geofence status
                    printGeofenceStatus(context)
                    
                    // Verify geofence registration
                    verifyGeofenceRegistration(context)
                    
                    // Start periodic location updates
                    startLocationUpdates(context)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding geofences: ${e.message}")
                    e.printStackTrace()
                    throw e
                }
                
            } catch (e: SecurityException) {
                Log.e(TAG, "Location permission denied: ${e.message}")
                throw SecurityException("Location permission denied: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting geofencing: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun stopGeofencing(context: Context) {
        try {
            geofencingClient?.removeGeofences(getGeofencePendingIntent(context))?.await()
            isGeofencingActive = false
            Log.d(TAG, "Geofencing stopped successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping geofencing: ${e.message}")
            throw e
        }
    }

    suspend fun restartGeofencing(context: Context) {
        Log.d(TAG, "Restarting geofencing...")
        stopGeofencing(context)
        startGeofencing(context)
    }

    fun isGeofencingActive(): Boolean = isGeofencingActive

    private fun createGeofenceRequest(): GeofencingRequest {
        val geofences = mallLocations.map { mall ->
            Geofence.Builder()
                .setRequestId(mall.id)
                .setCircularRegion(
                    mall.location.latitude,
                    mall.location.longitude,
                    mall.radius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                    Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .setNotificationResponsiveness(1000) // Reduced to 1 second for faster response
                .setLoiteringDelay(0) // No loitering delay
                .build()
        }.also { 
            Log.d(TAG, "Created ${it.size} geofences")
            it.forEach { geofence ->
                Log.d(TAG, """
                    Geofence Details:
                    ID: ${geofence.requestId}
                    Radius: ${geofence.radius}m
                    Responsiveness: ${geofence.notificationResponsiveness}ms
                    Loitering Delay: ${geofence.loiteringDelay}ms
                    Transitions: ${geofence.transitionTypes}
                """.trimIndent())
            }
        }

        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()
    }

    private fun hasRequiredPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createNotificationChannel(context: Context) {
        if (!isChannelCreated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Mall Geofence Notifications"
                val descriptionText = "Notifications for nearby malls"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableLights(true)
                    enableVibration(true)
                    setShowBadge(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                isChannelCreated = true
                Log.d(TAG, "Notification channel created with high importance")
            }
        }
    }

    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = "com.example.calender.ACTION_GEOFENCE_EVENT"
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun showNotification(context: Context, mallName: String, isEntering: Boolean) {
        Log.d(TAG, """
            Attempting to show notification:
            Mall: $mallName
            Type: ${if (isEntering) "ENTER" else "EXIT"}
            Time: ${System.currentTimeMillis()}
            Geofencing Active: $isGeofencingActive
            Channel Created: $isChannelCreated
        """.trimIndent())
        
        // Ensure channel is created before showing notification
        createNotificationChannel(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create an intent to open the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_map", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            mallName.hashCode(), // Use mall name as request code to create unique pending intents
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = if (isEntering) {
            "🎉 Special offers available at $mallName! Tap to view exclusive deals."
        } else {
            "💫 More offers await! Check out other malls nearby."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Shopping Deals Nearby!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setLights(0xFF0000FF.toInt(), 1000, 1000)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        try {
            notificationManager.notify(mallName.hashCode(), notification)
            Log.d(TAG, "Notification sent successfully for $mallName")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification: ${e.message}")
            e.printStackTrace()
        }
    }

    // Add this function to calculate distance between two points
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]  // Distance in meters
    }

    // Add this function to format distance nicely
    private fun formatDistance(meters: Float): String {
        return when {
            meters >= 1000 -> String.format("%.1f km", meters / 1000)
            else -> String.format("%.0f m", meters)
        }
    }

    // Update the status function to include distances
    suspend fun printGeofenceStatus(context: Context) {
        try {
            val geofencingClient = LocationServices.getGeofencingClient(context)
            // Get current location first
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            var currentLocation: android.location.Location? = null
            
            try {
                currentLocation = fusedLocationClient.lastLocation.await()
                currentLocation?.let {
                    Log.d(TAG, """
                        Current Location:
                        Latitude: ${it.latitude}
                        Longitude: ${it.longitude}
                        Accuracy: ${it.accuracy}m
                        Time: ${it.time}
                        ------------------------
                    """.trimIndent())
                } ?: Log.d(TAG, "Last location is null")
            } catch (e: Exception) {
                Log.e(TAG, "Error getting last location: ${e.message}")
            }

            // Log the active geofences with distances
            Log.d(TAG, "Active Geofences:")
            mallLocations.forEach { location ->
                val distance = currentLocation?.let {
                    calculateDistance(
                        it.latitude,
                        it.longitude,
                        location.location.latitude,
                        location.location.longitude
                    )
                }
                
                val distanceText = distance?.let { 
                    "Distance: ${formatDistance(it)}" 
                } ?: "Distance: Unknown (location not available)"
                
                val status = distance?.let {
                    when {
                        it <= location.radius -> "INSIDE geofence"
                        it <= location.radius + 100 -> "NEAR geofence (within 100m)"
                        else -> "OUTSIDE geofence"
                    }
                } ?: "Status: Unknown (location not available)"

                Log.d(TAG, """
                    Location: ${location.name}
                    ID: ${location.id}
                    Radius: ${formatDistance(location.radius)}
                    Coordinates: ${location.location.latitude}, ${location.location.longitude}
                    $distanceText
                    Status: $status
                    Geofencing Active: $isGeofencingActive
                    ------------------------
                """.trimIndent())
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking geofence status: ${e.message}")
        }
    }

    // Add a function to get distance to a specific mall
    suspend fun getDistanceToMall(context: Context, mallId: String): String {
        val mall = mallLocations.find { it.id == mallId } ?: return "Mall not found"
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        return try {
            val currentLocation = fusedLocationClient.lastLocation.await()
            currentLocation?.let {
                val distance = calculateDistance(
                    it.latitude,
                    it.longitude,
                    mall.location.latitude,
                    mall.location.longitude
                )
                formatDistance(distance)
            } ?: "Location not available"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun getMallName(geofenceId: String): String {
        return mallLocations.find { it.id == geofenceId }?.name ?: "Unknown Mall"
    }

    // Add this function for testing
    fun printMallLocations() {
        mallLocations.forEach { mall ->
            println("""
                Mall: ${mall.name}
                Location: ${mall.location.latitude}, ${mall.location.longitude}
                Radius: ${mall.radius} meters
                Test coordinates (just outside radius):
                - North: ${mall.location.latitude + (mall.radius + 50) / 111111.0}, ${mall.location.longitude}
                - South: ${mall.location.latitude - (mall.radius + 50) / 111111.0}, ${mall.location.longitude}
                - East: ${mall.location.latitude}, ${mall.location.longitude + (mall.radius + 50) / (111111.0 * Math.cos(Math.toRadians(mall.location.latitude)))}
                - West: ${mall.location.latitude}, ${mall.location.longitude - (mall.radius + 50) / (111111.0 * Math.cos(Math.toRadians(mall.location.latitude)))}
                Test coordinates (inside radius):
                - Center: ${mall.location.latitude}, ${mall.location.longitude}
                - North: ${mall.location.latitude + (mall.radius - 50) / 111111.0}, ${mall.location.longitude}
                - South: ${mall.location.latitude - (mall.radius - 50) / 111111.0}, ${mall.location.longitude}
                - East: ${mall.location.latitude}, ${mall.location.longitude + (mall.radius - 50) / (111111.0 * Math.cos(Math.toRadians(mall.location.latitude)))}
                - West: ${mall.location.latitude}, ${mall.location.longitude - (mall.radius - 50) / (111111.0 * Math.cos(Math.toRadians(mall.location.latitude)))}
                ------------------------
            """.trimIndent())
        }
    }

    // Filter locations by type
    fun getLocationsByType(type: LocationType): List<MallLocation> {
        return if (type == LocationType.ALL) {
            mallLocations
        } else {
            mallLocations.filter { it.type == type }
        }
    }

    // Filter locations by search query
    fun searchLocations(query: String): List<MallLocation> {
        val searchQuery = query.lowercase()
        return mallLocations.filter { location ->
            location.name.lowercase().contains(searchQuery) ||
            location.description.lowercase().contains(searchQuery) ||
            location.tags.any { it.lowercase().contains(searchQuery) }
        }
    }

    // Filter locations by multiple tags
    fun filterByTags(tags: List<String>): List<MallLocation> {
        return if (tags.isEmpty()) {
            mallLocations
        } else {
            mallLocations.filter { location ->
                tags.any { tag ->
                    location.tags.any { it.lowercase().contains(tag.lowercase()) }
                }
            }
        }
    }

    // Get all available tags
    fun getAllTags(): List<String> {
        return mallLocations
            .flatMap { it.tags }
            .distinct()
            .sorted()
    }

    // Add function to verify geofence registration
    private suspend fun verifyGeofenceRegistration(context: Context) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val lastLocation = fusedLocationClient.lastLocation.await()
            
            lastLocation?.let { location ->
                Log.d(TAG, """
                    Current Location Status:
                    Latitude: ${location.latitude}
                    Longitude: ${location.longitude}
                    Accuracy: ${location.accuracy}m
                    Time: ${location.time}
                    Provider: ${location.provider}
                    Has Speed: ${location.hasSpeed()}
                    Has Bearing: ${location.hasBearing()}
                    Is From Mock Provider: ${location.isFromMockProvider}
                """.trimIndent())

                // Check distance to each geofence
                mallLocations.forEach { mall ->
                    val distance = calculateDistance(
                        location.latitude,
                        location.longitude,
                        mall.location.latitude,
                        mall.location.longitude
                    )
                    val status = when {
                        distance <= mall.radius -> "INSIDE geofence"
                        distance <= mall.radius + 100 -> "NEAR geofence (within 100m)"
                        else -> "OUTSIDE geofence"
                    }
                    Log.d(TAG, """
                        Mall: ${mall.name}
                        Distance: ${formatDistance(distance)}
                        Status: $status
                        Geofence Radius: ${formatDistance(mall.radius)}
                        Should Trigger: ${distance <= mall.radius}
                    """.trimIndent())
                }
            } ?: Log.w(TAG, "Last location is null - geofencing might not work properly")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying geofence registration: ${e.message}")
        }
    }

    private fun startLocationUpdates(context: Context) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 5000 // 5 seconds
                fastestInterval = 2000 // 2 seconds
                smallestDisplacement = 5f // 5 meters
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        locationResult.lastLocation?.let { location ->
                            Log.d(TAG, """
                                Location Update Received:
                                Latitude: ${location.latitude}
                                Longitude: ${location.longitude}
                                Accuracy: ${location.accuracy}m
                                Time: ${location.time}
                                Provider: ${location.provider}
                            """.trimIndent())

                            // Check distance to each mall
                            mallLocations.forEach { mall ->
                                val distance = calculateDistance(
                                    location.latitude,
                                    location.longitude,
                                    mall.location.latitude,
                                    mall.location.longitude
                                )
                                val status = when {
                                    distance <= mall.radius -> "INSIDE geofence"
                                    distance <= mall.radius + 100 -> "NEAR geofence (within 100m)"
                                    else -> "OUTSIDE geofence"
                                }
                                Log.d(TAG, """
                                    Distance to ${mall.name}:
                                    Distance: ${formatDistance(distance)}
                                    Status: $status
                                    Should Trigger: ${distance <= mall.radius}
                                """.trimIndent())
                            }
                        }
                    }
                },
                context.mainLooper
            )
            Log.d(TAG, "Location updates started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting location updates: ${e.message}")
        }
    }
} 