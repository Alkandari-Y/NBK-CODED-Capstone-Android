package com.coded.capstone.MapAndGeofencing

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.coded.capstone.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest

class GeofenceService : Service() {
    private val TAG = "GeofenceService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val NOTIFICATION_ID = 2
    private val CHANNEL_ID = "geofence_service_channel"
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "GeofenceService onCreate")
        // Permission check before proceeding
        val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasBackgroundLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasForegroundServiceLocation = if (android.os.Build.VERSION.SDK_INT >= 34) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
        if (!(hasFineLocation && hasBackgroundLocation && hasForegroundServiceLocation)) {
            Log.w(TAG, "Stopping GeofenceService: required location permissions not granted.")
            stopSelf()
            return
        }
        createNotificationChannel()
        acquireWakeLock()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "GeofenceService onStartCommand")
        serviceScope.launch {
            try {
                if (!GeofenceManager.isGeofencingActive()) {
                    Log.d(TAG, "Starting geofencing in service")
                    GeofenceManager.startGeofencing(applicationContext)
                } else {
                    Log.d(TAG, "Geofencing already active")
                }
                
                while (true) {
                    verifyGeofenceRegistration()
                    delay(5 * 60 * 1000L) // Check every 5 minutes
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in geofence service: ${e.message}")
                restartService()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "GeofenceService onDestroy")
        serviceScope.launch {
            try {
                GeofenceManager.stopGeofencing(applicationContext)
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping geofencing in service: ${e.message}")
            }
        }
        releaseWakeLock()
        super.onDestroy()
        
        restartService()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "GeofenceService onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        // Restart the service if it's killed
        restartService()
    }

    private fun restartService() {
        Log.d(TAG, "Attempting to restart service")
        val intent = Intent(applicationContext, GeofenceService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Calender::GeofenceServiceWakeLock"
        ).apply {
            acquire() // Indefinite wake lock
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    private suspend fun verifyGeofenceRegistration() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
            val lastLocation = fusedLocationClient.lastLocation.await()
            
            lastLocation?.let { location ->
                Log.d(TAG, """
                    Current Location Status:
                    Latitude: ${location.latitude}
                    Longitude: ${location.longitude}
                    Accuracy: ${location.accuracy}m
                    Time: ${location.time}
                    Provider: ${location.provider}
                """.trimIndent())

                // If geofencing is not active, restart it
                if (!GeofenceManager.isGeofencingActive()) {
                    Log.d(TAG, "Geofencing not active, restarting...")
                    GeofenceManager.startGeofencing(applicationContext)
                }
            } ?: Log.w(TAG, "Last location is null - geofencing might not work properly")
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying geofence registration: ${e.message}")
            // Try to restart geofencing if there's an error
            try {
                GeofenceManager.startGeofencing(applicationContext)
            } catch (e: Exception) {
                Log.e(TAG, "Error restarting geofencing: ${e.message}")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Geofence Service"
            val descriptionText = "Keeps geofencing active in the background"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mall Deals Active")
            .setContentText("You'll be notified about nearby mall deals")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
    }
} 