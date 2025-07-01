package com.coded.capstone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.coded.capstone.MapAndGeofencing.LocationPermissionHandler
import com.coded.capstone.MapAndGeofencing.GeofenceManager
import com.coded.capstone.deeplink.DeepLinkHandler
import com.coded.capstone.managers.GeofencePreferenceManager
import com.coded.capstone.navigation.AppHost
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.ui.theme.CapstoneTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissionRequestCode = 101


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFirebaseNotificationPermission()
        if (!hasBluetoothPermissions(this)) {
            ActivityCompat.requestPermissions(this, getBluetoothPermissions(), permissionRequestCode)
        }

        GeofencePreferenceManager.ensureDefaultDisabled(this)

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
                                    if (GeofencePreferenceManager.isGeofencingEnabled(applicationContext)) {
                                        lifecycleScope.launch {
                                            GeofenceManager.startGeofencing(applicationContext)
                                        }
                                    }
                                }
                            )
                            AppHost()
                        }
                    }
                }
            }
        }
        
        // Handle deep link if app was launched via deep link
        handleDeepLink(intent)
    }
    
    /**
     * Handle deep links when app is already running
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
    
    /**
     * Process deep link intent
     */
    private fun handleDeepLink(intent: Intent) {
        // Note: You'll need to pass the NavController from AppHost
        // For now, this is a placeholder that can be connected later
        // DeepLinkHandler.handleDeepLink(intent, navController, this)
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun getBluetoothPermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        return permissions.toTypedArray()
    }

    fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ).all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}

