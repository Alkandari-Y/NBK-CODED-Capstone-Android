package com.coded.capstone.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.MapAndGeofencing.GeofenceManager
import com.coded.capstone.MapAndGeofencing.LocationManager
import com.coded.capstone.R
import com.coded.capstone.SVG.BluetoothSolidIcon
import android.content.Intent
import com.coded.capstone.MapAndGeofencing.GeofenceService
import com.coded.capstone.managers.BlePreferenceManager
import com.coded.capstone.services.BleScanService
import com.coded.capstone.managers.GeofencePreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Roboto font family
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isBluetoothEnabled by remember {
        mutableStateOf(BlePreferenceManager.isBleEnabled(context))
    }

    var isGeofencingEnabled by remember {
        mutableStateOf(
            GeofencePreferenceManager.isGeofencingEnabled(context)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // FIXED HEADER - No circle, centered title, consistent blue, more spacing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                // FIXED: More spacing from top
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // FIXED: Clean back button - no circle background
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF8EC5FF), // FIXED: Consistent blue shade
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // FIXED: Properly centered title
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // FIXED: More compact settings content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // FIXED: Reduced spacing
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // FIXED: Compact Bluetooth Settings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp), // FIXED: Less rounded
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp) // FIXED: Less elevation
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // FIXED: Reduced padding
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon in colored container
                        Box(
                            modifier = Modifier
                                .size(44.dp) // FIXED: Smaller icon container
                                .background(
                                    if (isBluetoothEnabled) Color(0xFF8EC5FF).copy(alpha = 0.1f)
                                    else Color(0xFF6B7280).copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            BluetoothSolidIcon(
                                modifier = Modifier.size(20.dp), // FIXED: Smaller icon
                                color = if (isBluetoothEnabled) Color(0xFF8EC5FF) else Color(0xFF6B7280)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Content
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Blue Deals",
                                fontSize = 16.sp, // FIXED: Smaller title
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF23272E),
                                fontFamily = RobotoFont
                            )
                            Text(
                                text = if (isBluetoothEnabled) "Find nearby partners" else "Enable to find partners",
                                fontSize = 13.sp, // FIXED: Smaller, cleaner description
                                color = Color(0xFF6B7280),
                                fontFamily = RobotoFont
                            )
                        }

                        // FIXED: Smaller, cleaner toggle
                        Switch(
                            checked = isBluetoothEnabled,
                            onCheckedChange = { enabled ->
                                isBluetoothEnabled = enabled
                                BlePreferenceManager.setBleEnabled(context, enabled)

                                val intent = Intent(context, BleScanService::class.java)
                                if (enabled) {
                                    context.startService(intent)
                                } else {
                                    context.stopService(intent)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF8EC5FF),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFF6B7280).copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.size(width = 44.dp, height = 24.dp) // FIXED: Smaller switch
                        )
                    }
                }
            }

            // FIXED: Compact Geofencing Settings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon in colored container
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isGeofencingEnabled) Color(0xFF8EC5FF).copy(alpha = 0.1f)
                                    else Color(0xFF6B7280).copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isGeofencingEnabled) Color(0xFF8EC5FF) else Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Content
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Geo Offers",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF23272E),
                                fontFamily = RobotoFont
                            )
                            Text(
                                text = if (isGeofencingEnabled) "Get location-based offers" else "Enable location offers",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                fontFamily = RobotoFont
                            )
                        }

                        // Toggle switch
                        Switch(
                            checked = isGeofencingEnabled,
                            onCheckedChange = { enabled ->
                                isGeofencingEnabled = enabled
                                GeofencePreferenceManager.setGeofencingEnabled(context, enabled)

                                if (enabled) {
                                    LocationManager.startGeofenceService(context)
                                } else {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        GeofenceManager.stopGeofencing(context)
                                    }
                                    context.stopService(Intent(context, GeofenceService::class.java))
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF8EC5FF),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFF6B7280).copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.size(width = 44.dp, height = 24.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
            }
        }
    }
}