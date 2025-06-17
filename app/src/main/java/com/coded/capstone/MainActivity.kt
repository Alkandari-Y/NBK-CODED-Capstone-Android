package com.coded.capstone

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
import androidx.core.view.WindowCompat
import com.coded.capstone.MapAndGeofencing.LocationPermissionHandler
import com.coded.capstone.ui.theme.CapstoneTheme
import com.coded.capstone.navigation.AppHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CapstoneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MaterialTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
//                            LocationPermissionHandler(
//                                onPermissionGranted = {
//                                    // Permissions granted, geofence service will be started by LocationPermissionHandler
//                                }
//                            )
                            AppHost()
                        }
                    }
                }
            }
        }
    }
}

