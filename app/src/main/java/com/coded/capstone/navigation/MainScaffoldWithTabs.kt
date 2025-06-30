package com.coded.capstone.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.coded.capstone.Screens.Wallet.WalletScreen
import com.coded.capstone.composables.home.BottomNavBar
import com.coded.capstone.composables.home.DrawerContent
import com.coded.capstone.screens.calender.CalendarScreen
import com.coded.capstone.screens.home.HomeScreen
import com.coded.capstone.screens.recommendation.RecommendationScreen
import com.coded.capstone.viewModels.AuthViewModel
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.RecommendationViewModel

@Composable
fun MainScaffoldWithTabs(
    navController: NavController,
    authViewModel: AuthViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val context = LocalContext.current
    val recommendationViewModel = remember { RecommendationViewModel(context) }
    val windowSize = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> HomeScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onNotificationClick = {
                        navController.navigate(NavRoutes.NAV_ROUTE_NOTIFICATIONS)
                    },
                    onAccountClick = { accountId ->
                        navController.navigate(NavRoutes.accountDetailRoute(accountId))
                    },
                    onViewAllAccounts = {
                        navController.navigate(NavRoutes.NAV_ROUTE_ACCOUNT_VIEW_ALL)
                    }
                )
                1 -> WalletScreen(navController = navController)
                2 -> CalendarScreen(viewModel = recommendationViewModel, navController = navController)
                3 -> RecommendationScreen(viewModel = homeScreenViewModel)
            }
        }

        // Bottom Navigation Bar overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    }
}