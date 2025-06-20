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
import androidx.navigation.NavController
import com.coded.capstone.Wallet.WalletScreen
import com.coded.capstone.composables.home.BottomNavBar
import com.coded.capstone.screens.CalendarScreen
import com.coded.capstone.screens.home.HomeScreen
import com.coded.capstone.screens.recommendation.RecommendationScreen
import com.coded.capstone.viewModels.AuthViewModel
import com.coded.capstone.viewModels.HomeScreenViewModel

@Composable
fun MainScaffoldWithTabs(
    navController: NavController,
    authViewModel: AuthViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(navController,authViewModel, onAccountClick = { accountId ->
                    navController.navigate(NavRoutes.accountDetailRoute(accountId))
                }, onViewAllAccounts = {
                    navController.navigate(NavRoutes.NAV_ROUTE_ACCOUNT_VIEW_ALL)
                },)
                1 -> WalletScreen()
                2 -> CalendarScreen()
                3 -> RecommendationScreen(viewModel = homeScreenViewModel)
            }
        }
    }
}