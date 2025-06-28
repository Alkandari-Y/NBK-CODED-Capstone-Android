package com.coded.capstone.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight

@Composable
fun DrawerContent(
    userName: String,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(230.dp),
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            }
        ) { scaffoldPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 100.dp
                        ),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8EC5FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.first().toString(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A2A2A)
                        )
                        Text(
                            text = "KLUE Banking",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8EC5FF)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        HorizontalDivider(
                            color = Color(0xFF2A2A2A).copy(alpha = 0.1f),
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Menu items
                        DrawerMenuItem(
                            icon = Icons.Default.Person,
                            title = "Profile",
                            onClick = onProfileClick,
                            color = Color(0xFF8EC5FF)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DrawerMenuItem(
                            icon = Icons.Default.Settings,
                            title = "Settings",
                            onClick = onSettingsClick,
                            color = Color(0xFF8EC5FF)

                        )
                    }

                    // Bottom section with logout
                    Column {
                        HorizontalDivider(
                            color = Color(0xFF2A2A2A).copy(alpha = 0.1f),
                            thickness = 1.dp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DrawerMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = "Logout",
                            onClick = onLogoutClick,
                            isDestructive = true
                        )
                    }
                }
            }
        }
    }
}