package com.coded.capstone.MapAndGeofencing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.coded.capstone.managers.GeofencePreferenceManager

class LocationManager(private val context: Context) {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002

        fun hasLocationPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasBackgroundLocationPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasForegroundServiceLocationPermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= 34) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        }

        fun hasNotificationPermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }

        fun requestLocationPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        fun requestNotificationPermission(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        fun startGeofenceService(context: Context) {
            if (GeofencePreferenceManager.isGeofencingEnabled(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(Intent(context, GeofenceService::class.java))
                } else {
                    context.startService(Intent(context, GeofenceService::class.java))
                }
            } else {
                Log.d("LocationManager", "Geofencing disabled in preferences; service not started.")
            }
        }
    }
}

@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasLocationPermission by remember { mutableStateOf(LocationManager.hasLocationPermission(context)) }
    var hasBackgroundLocationPermission by remember { mutableStateOf(LocationManager.hasBackgroundLocationPermission(context)) }
    var hasForegroundServiceLocationPermission by remember { mutableStateOf(LocationManager.hasForegroundServiceLocationPermission(context)) }
    var hasNotificationPermission by remember { mutableStateOf(LocationManager.hasNotificationPermission(context)) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted && hasBackgroundLocationPermission && hasForegroundServiceLocationPermission && hasNotificationPermission) {
            onPermissionGranted()
        }
    }

    val backgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasBackgroundLocationPermission = isGranted
        if (isGranted && hasLocationPermission && hasForegroundServiceLocationPermission && hasNotificationPermission) {
            onPermissionGranted()
        }
    }

    val foregroundServiceLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasForegroundServiceLocationPermission = isGranted
        if (isGranted && hasLocationPermission && hasBackgroundLocationPermission && hasNotificationPermission) {
            onPermissionGranted()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted && hasLocationPermission && hasBackgroundLocationPermission && hasForegroundServiceLocationPermission) {
            onPermissionGranted()
        }
    }

    // Request permissions if not granted
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!hasBackgroundLocationPermission) {
            backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= 34 && !hasForegroundServiceLocationPermission) {
            foregroundServiceLocationPermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (hasLocationPermission && hasBackgroundLocationPermission && hasForegroundServiceLocationPermission && hasNotificationPermission) {
            onPermissionGranted()
        }
    }

    // Start geofence service when app starts
    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (hasLocationPermission && hasBackgroundLocationPermission && hasForegroundServiceLocationPermission && hasNotificationPermission) {
                    LocationManager.startGeofenceService(context)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }
} 