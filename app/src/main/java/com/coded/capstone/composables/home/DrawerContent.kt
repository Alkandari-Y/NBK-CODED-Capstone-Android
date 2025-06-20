package com.coded.capstone.composables.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerContent(
    userName: String,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = Color(0xFFFAFAFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with user info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                // User avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
                            )
                        ),
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = "KLUE Banking",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7F8C8D)
                )
            }

            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Menu items
            DrawerMenuItem(
                icon = Icons.Default.Person,
                title = "Profile",
                onClick = onProfileClick
            )

            DrawerMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout at bottom
            DrawerMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Logout",
                onClick = onLogoutClick,
                isDestructive = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}