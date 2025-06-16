package com.coded.capstone.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coded.capstone.composables.ui.TopBar
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.respositories.UserRepository

@Composable
fun DashboardScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit,

) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (UserRepository.userInfo == null) {
            UserRepository.loadUserInfo(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(
            onLogoutClick = {
                TokenManager.clearToken(context)
                onLogoutClick()
            }
        )

        Text(
            text = "KLUE HOME SCREEN",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00BCD4)
            )
        )
    }

}