package com.coded.capstone.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.coded.capstone.viewModels.HomeScreenViewModel
import kotlinx.coroutines.delay

@Composable
fun LoadingDashboardScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        delay(1500L)
        // viewModel.fetchAccounts() removed; HomeScreen will fetch accounts
        navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
            popUpTo(NavRoutes.NAV_ROUTE_LOADING_DASHBOARD) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
