package com.coded.capstone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.coded.capstone.MapAndGeofencing.LocationPermissionHandler
import com.coded.capstone.MapAndGeofencing.GeofenceManager
import com.coded.capstone.navigation.AppHost
import com.coded.capstone.ui.theme.CapstoneTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                            LocationPermissionHandler(
                                onPermissionGranted = {
                                    // Permissions granted, so we start the geofence service directly.
                                    CoroutineScope(Dispatchers.Main).launch {
                                        GeofenceManager.startGeofencing(applicationContext)
                                    }
                                }
                            )
                            AppHost()
                        }
                    }
                }
            }
        }
    }
}

