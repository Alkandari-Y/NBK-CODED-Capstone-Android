package com.coded.capstone.composables.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = Color.White,
        indicatorColor = Color(0xFF6366F1).copy(alpha = 0.3f), // Purple indicator matching the theme
        unselectedIconColor = Color.White.copy(alpha = 0.6f),
        unselectedTextColor = Color.White.copy(alpha = 0.6f)
    )

    NavigationBar(
        containerColor = Color(0xFF1E293B), // Matching the middle gradient color from your screen
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = navItemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet") },
            label = { Text("Wallet") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = navItemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = navItemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.LocalOffer, contentDescription = "Recommend") },
            label = { Text("Recommend") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = navItemColors
        )
    }
}