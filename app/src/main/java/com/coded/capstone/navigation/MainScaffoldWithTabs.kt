package com.coded.capstone.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.coded.capstone.Screens.Wallet.WalletScreen
import com.coded.capstone.composables.home.BottomNavBar
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
    homeScreenViewModel: HomeScreenViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val recommendationViewModel = remember { RecommendationViewModel(context) }

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(navController, authViewModel, onAccountClick = { accountId ->
                    navController.navigate(NavRoutes.accountDetailRoute(accountId))
                }, onViewAllAccounts = {
                    navController.navigate(NavRoutes.NAV_ROUTE_ACCOUNT_VIEW_ALL)
                })
                1 -> WalletScreen(navController = navController)
                2 -> CalendarScreen(viewModel = recommendationViewModel)
                3 -> RecommendationScreen(viewModel = homeScreenViewModel)
            }
        }
    }
}