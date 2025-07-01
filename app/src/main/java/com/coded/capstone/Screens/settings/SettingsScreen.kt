package com.coded.capstone.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.SVG.BluetoothSolidIcon
import android.content.Intent
import com.coded.capstone.managers.BlePreferenceManager
import com.coded.capstone.services.BleScanService

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header Card - matching profile design
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8EC5FF).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF8EC5FF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                // Extra space to extend the dark section
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Settings Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Bluetooth Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isBluetoothEnabled) {
                                BluetoothSolidIcon(
                                    modifier = Modifier.size(28.dp),
                                    color = Color(0xFF8EC5FF)
                                )
                            } else {
                                BluetoothSolidIcon(
                                    modifier = Modifier.size(28.dp),
                                    color = Color(0xFF6B7280)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Bluetooth",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF23272E),
                                    fontFamily = RobotoFont
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bluetooth Toggle Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isBluetoothEnabled) "Bluetooth Enabled" else "Bluetooth Disabled",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF23272E),
                                        fontFamily = RobotoFont
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBluetoothEnabled) 
                                        "Find nearby deals"
                                    else 
                                        "Enable Bluetooth to search for nearby partners",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF6B7280),
                                        fontFamily = RobotoFont
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Toggle Switch
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
                                    uncheckedTrackColor = Color(0xFF6B7280)
                                )
                            )
                        }

                        // Status indicator
                        if (isBluetoothEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            Color(0xFF10B981),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ready to connect",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF10B981),
                                        fontFamily = RobotoFont
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Additional Settings Card (placeholder for future settings)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "More Settings",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF23272E),
                                fontFamily = RobotoFont
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Additional settings will be available here in future updates",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280),
                                fontFamily = RobotoFont
                            )
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