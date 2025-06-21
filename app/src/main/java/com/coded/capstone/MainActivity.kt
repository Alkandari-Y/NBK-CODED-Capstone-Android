package com.coded.capstone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.coded.capstone.MapAndGeofencing.LocationPermissionHandler
import com.coded.capstone.ui.theme.CapstoneTheme
import com.coded.capstone.navigation.AppHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFirebaseNotificationPermission()

        enableEdgeToEdge()

        setContent {
            CapstoneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MaterialTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            LocationPermissionHandler(
                                onPermissionGranted = {
                                    // Permissions granted, geofence service will be started by LocationPermissionHandler
                                }
                            )
                            AppHost()
                        }
                    }
                }
            }
        }
    }

    private fun requestFirebaseNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}

