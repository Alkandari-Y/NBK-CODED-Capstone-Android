package com.coded.capstone.MapAndGeofencing

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import android.graphics.Color
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import com.coded.capstone.R
import com.coded.capstone.MapAndGeofencing.GeofenceManager.filterByTags
import com.coded.capstone.MapAndGeofencing.GeofenceManager.getAllTags
import com.coded.capstone.MapAndGeofencing.GeofenceManager.getLocationsByType
import com.coded.capstone.MapAndGeofencing.GeofenceManager.mallLocations
import com.coded.capstone.MapAndGeofencing.GeofenceManager.searchLocations
import androidx.compose.ui.graphics.Color as ComposeColor


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedLocation by remember { mutableStateOf<MallLocation?>(null) }
    var distanceToSelectedLocation by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(LocationType.ALL) }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var geofencingStatus by remember { mutableStateOf("Not Started") }
    
    // Add state for current location
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    
    val allTags = remember { getAllTags() }
    
    // Add logging for debugging
    val TAG = "MapScreen"
    
    // Filter locations based on search, type, and tags
    val filteredLocations = remember(searchQuery, selectedType, selectedTags) {
        var locations = mallLocations
        
        if (selectedType != LocationType.ALL) {
            locations = getLocationsByType(selectedType)
        }
        
        if (searchQuery.isNotEmpty()) {
            locations = searchLocations(searchQuery)
        }
        
        if (selectedTags.isNotEmpty()) {
            locations = filterByTags(selectedTags)
        }
        
        locations
    }

    // Kuwait City coordinates (fallback)
    val kuwaitCity = LatLng(29.3759, 47.9774)
    
    // Custom map style
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }


    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kuwaitCity, 11f)
    }

    // Location permissions setup
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val backgroundLocationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    // Permission checks
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasBackgroundLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Request permissions
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionState.launchPermissionRequest()
        }
        // Only request background location after foreground location is granted
        if (hasLocationPermission && !hasBackgroundLocationPermission) {
            backgroundLocationPermissionState.launchPermissionRequest()
        }
    }

    // Show permission request dialog if needed
    if (!hasLocationPermission) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Location Permission Required") },
            text = { Text("This app needs location permission to show your position on the map and provide geofencing notifications. Please grant location permission to continue.") },
            confirmButton = {
                Button(
                    onClick = { locationPermissionState.launchPermissionRequest() }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = onClose) {
                    Text("Close")
                }
            }
        )
    }

    // Show background location request dialog if needed
    if (hasLocationPermission && !hasBackgroundLocationPermission) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Background Location Required") },
            text = { Text("To receive notifications when you enter or exit mall areas, please grant background location permission.") },
            confirmButton = {
                Button(
                    onClick = { backgroundLocationPermissionState.launchPermissionRequest() }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("Not Now")
                }
            }
        )
    }

    // Update geofencing status based on permissions
    LaunchedEffect(hasLocationPermission, hasBackgroundLocationPermission) {
        if (hasLocationPermission && hasBackgroundLocationPermission) {
            try {
                if (!GeofenceManager.isGeofencingActive()) {
                    GeofenceManager.startGeofencing(context)
                    geofencingStatus = "Active"
                } else {
                    geofencingStatus = "Already Active"
                }
            } catch (e: Exception) {
                geofencingStatus = "Error: ${e.message}"
            }
        } else {
            geofencingStatus = "Permissions Required"
        }
    }

    // Update distance when location is selected
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            distanceToSelectedLocation = GeofenceManager.getDistanceToMall(context, location.id)
        } ?: run {
            distanceToSelectedLocation = null
        }
    }

    // Update distance periodically
    LaunchedEffect(Unit) {
        while (true) {
            selectedLocation?.let { location ->
                distanceToSelectedLocation = GeofenceManager.getDistanceToMall(context, location.id)
            }
            kotlinx.coroutines.delay(5000) // Update every 5 seconds
        }
    }

    // Get current location when map opens
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            try {
                withContext(Dispatchers.IO) {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    val location = fusedLocationClient.lastLocation.await()
                    location?.let {
                        currentLocation = LatLng(it.latitude, it.longitude)
                        withContext(Dispatchers.Main) {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    currentLocation!!,
                                    15f
                                ),
                                durationMs = 1000
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MapScreen", "Error getting current location: ${e.message}")
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding() // Add padding for system bars
    ) {
        // Map with padding
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // Add padding for the bottom card
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapStyleOptions = mapStyle,
                isIndoorEnabled = true,
                isTrafficEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true,
                zoomGesturesEnabled = true
            ),
            onMapClick = { latLng ->
                android.util.Log.d(TAG, "Map clicked at: ${latLng.latitude}, ${latLng.longitude}")
                scope.launch {
                    try {
                        val newPosition = CameraPosition.Builder()
                            .target(latLng)
                            .zoom(15f)
                            .build()
                        
                        cameraPositionState.position = newPosition
                        android.util.Log.d(TAG, "Camera position updated to: ${newPosition.target.latitude}, ${newPosition.target.longitude}, zoom: ${newPosition.zoom}")
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "Error updating camera position: ${e.message}")
                    }
                }
            }
        ) {
            // Add markers for filtered locations
            filteredLocations.forEach { location ->
                Marker(
                    state = MarkerState(position = location.location),
                    title = location.name,
                    snippet = location.description,
                    onClick = {
                        selectedLocation = location
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    location.location,
                                    15f
                                ),
                                durationMs = 1000
                            )
                        }
                        true
                    }
                )
            }
        }

        // Top Bar with Search
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Search Bar with Filter Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search locations...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )
                    
                    // Filter Button
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Active Filters Display
                if (selectedType != LocationType.ALL || selectedTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (selectedType != LocationType.ALL) {
                            FilterChip(
                                selected = true,
                                onClick = { selectedType = LocationType.ALL },
                                label = { Text(selectedType.name) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            )
                        }
                        selectedTags.forEach { tag ->
                            FilterChip(
                                selected = true,
                                onClick = { selectedTags = selectedTags - tag },
                                label = { Text(tag) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Remove")
                                }
                            )
                        }
                    }
                }
            }
        }

        // Selected Location Card
        selectedLocation?.let { location ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { selectedLocation = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = location.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Add distance information
                    distanceToSelectedLocation?.let { distance ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Distance",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "You are $distance away",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        location.tags.forEach { tag ->
                            AssistChip(
                                onClick = { },
                                label = { Text(tag) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }

                    /*
                     * ----------------------------------------------------------------------------------
                     * --- COMMENTED OUT: Local Notification Test Buttons ---
                     *
                     * The following buttons were used to test local notifications directly
                     * from the device. Since this logic is now disabled in GeofenceManager
                     * and handled by the backend, these test buttons are no longer needed.
                     * ----------------------------------------------------------------------------------
                     */
                    // Spacer(modifier = Modifier.height(16.dp))
                    // Row(
                    //     modifier = Modifier.fillMaxWidth(),
                    //     horizontalArrangement = Arrangement.spacedBy(8.dp)
                    // ) {
                    //     OutlinedButton(
                    //         onClick = {
                    //             scope.launch {
                    //                 // GeofenceManager.showNotification(context, location.name, true)
                    //             }
                    //         },
                    //         modifier = Modifier.weight(1f)
                    //     ) {
                    //         Icon(
                    //             imageVector = Icons.Default.Notifications,
                    //             contentDescription = "Test Enter Notification",
                    //             modifier = Modifier.size(18.dp)
                    //         )
                    //         Spacer(modifier = Modifier.width(4.dp))
                    //         Text("Test Enter")
                    //     }
                    //     OutlinedButton(
                    //         onClick = {
                    //             scope.launch {
                    //                 // GeofenceManager.showNotification(context, location.name, false)
                    //             }
                    //         },
                    //         modifier = Modifier.weight(1f)
                    //     ) {
                    //         Icon(
                    //             imageVector = Icons.Default.ExitToApp,
                    //             contentDescription = "Test Exit Notification",
                    //             modifier = Modifier.size(18.dp)
                    //         )
                    //         Spacer(modifier = Modifier.width(4.dp))
                    //         Text("Test Exit")
                    //     }
                    // }
                    
                    // Special CODED Academy test button
                    if (location.id == "coded_academy") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // OutlinedButton(
                            //     onClick = {
                            //         scope.launch {
                            //             // GeofenceManager.testCodedAcademyNotification(context)
                            //         }
                            //     },
                            //     modifier = Modifier.weight(1f),
                            //     colors = ButtonDefaults.outlinedButtonColors(
                            //         containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            //     )
                            // ) {
                            //     Icon(
                            //         imageVector = Icons.Default.School,
                            //         contentDescription = "Test CODED Notification",
                            //         modifier = Modifier.size(18.dp)
                            //     )
                            //     Spacer(modifier = Modifier.width(4.dp))
                            //     Text("Test CODED")
                            // }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val status = GeofenceManager.checkCodedAcademyStatus(context)
                                        android.util.Log.d("MapScreen", "CODED Status: $status")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Check CODED Status",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Check Status")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        // OutlinedButton(
                        //     onClick = {
                        //         scope.launch {
                        //             // GeofenceManager.forceTriggerCodedAcademy(context)
                        //         }
                        //     },
                        //     modifier = Modifier.fillMaxWidth(),
                        //     colors = ButtonDefaults.outlinedButtonColors(
                        //         containerColor = MaterialTheme.colorScheme.errorContainer
                        //     )
                        // ) {
                        //     Icon(
                        //         imageVector = Icons.Default.PlayArrow,
                        //         contentDescription = "Force Trigger CODED",
                        //         modifier = Modifier.size(18.dp)
                        //     )
                        //     Spacer(modifier = Modifier.width(4.dp))
                        //     Text("Force Trigger CODED Geofence")
                        // }
                    }
                }
            }
        }

        // Close Button
        FloatingActionButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }

        // Filter Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        "Filter Locations",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Location Type Filter
                    Text(
                        "Location Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LocationType.values().forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.name) }
                            )
                        }
                    }

                    // Tags Filter
                    Text(
                        "Tags",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(allTags) { tag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedTags.contains(tag),
                                    onCheckedChange = { checked ->
                                        selectedTags = if (checked) {
                                            selectedTags + tag
                                        } else {
                                            selectedTags - tag
                                        }
                                    }
                                )
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedType = LocationType.ALL
                                selectedTags = emptyList()
                                showFilterSheet = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear All")
                        }
                        Button(
                            onClick = { showFilterSheet = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }
    }
} 