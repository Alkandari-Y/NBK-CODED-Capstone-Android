package com.coded.capstone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.screens.authentication.SignUpScreen
import com.coded.capstone.screens.authentication.LoginScreen
import com.coded.capstone.viewModels.AuthViewModel
import com.coded.capstone.screens.CalendarScreen
import com.coded.capstone.Wallet.WalletScreen
//import com.coded.capstone.screens.OffersScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.coded.capstone.screens.onboarding.CategoryOnBoarding
import com.coded.capstone.composables.home.BottomNavBar
import com.coded.capstone.screens.kyc.KycScreen
import com.coded.capstone.screens.onboarding.VendorsOnBoarding
import com.coded.capstone.screens.recommendation.RecommendationScreen
import com.coded.capstone.viewModels.KycViewModel


object NavRoutes {
    const val NAV_ROUTE_LOGIN = "login"
    const val NAV_ROUTE_SIGNUP = "signup"
    const val NAV_ROUTE_LOADING_DASHBOARD = "loading_dashboard"
    const val NAV_ROUTE_HOME = "home"
    const val NAV_ROUTE_WALLET ="wallet"
    const val NAV_ROUTE_CALENDER ="calender"
    const val NAV_ROUTE_RECOMMENDATIONS = "recommendations"
    const val NAV_ROUTE_FORGOT_PASSWORD = "forgot_password"
    const val NAV_ROUTE_CATEGORY_ONBOARDING = "category_onboarding"
    const val NAV_ROUTE_VENDORS_ONBOARDING = "vendors_onboarding"
    const val NAV_ROUTE_CREATE_ACCOUNT = "accounts/create"
    const val NAV_ROUTE_ACCOUNT_DETAILS = "accounts/manage/{accountNum}"
    const val NAV_ROUTE_ACCOUNT_VIEW_ALL = "accounts"

    const val NAV_ROUTE_EDIT_KYC = "/kyc"


    fun accountDetailRoute(accountNum: String) = "accounts/manage/$accountNum"

}



@Composable
fun AppHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (
            TokenManager.getToken(context) != null &&
            TokenManager.isRememberMeEnabled(context) &&
            !TokenManager.isAccessTokenExpired(context)
        ) {
            navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                popUpTo(0)
            }
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoutes.NAV_ROUTE_LOGIN
    ) {
        composable(NavRoutes.NAV_ROUTE_LOGIN) {
            val authViewModel = remember { AuthViewModel(context) }
            LoginScreen(
                authViewModel,
                navController
            )
        }
        composable(NavRoutes.NAV_ROUTE_SIGNUP) {
            val authViewModel = remember { AuthViewModel(context) }
            SignUpScreen(
                authViewModel,
                navController
            )
        }
        composable(NavRoutes.NAV_ROUTE_LOADING_DASHBOARD) {
            LoadingDashboardScreen(
                navController = navController,
            )
        }

        composable(NavRoutes.NAV_ROUTE_EDIT_KYC) {
            val kycViewModel = remember { KycViewModel(context) }
            KycScreen(
                navController = navController,
                viewModel= kycViewModel)
        }
        composable(NavRoutes.NAV_ROUTE_HOME) {
            val authViewModel = remember { AuthViewModel(context) }
            MainScaffoldWithTabs(navController , authViewModel)
        }
        composable(NavRoutes.NAV_ROUTE_CATEGORY_ONBOARDING) {
            CategoryOnBoarding(navController = navController)
        }
        composable("vendors_onboarding/{selectedCategories}") { backStackEntry ->
            val selectedCategoriesString = backStackEntry.arguments?.getString("selectedCategories") ?: ""
            val selectedCategories = if (selectedCategoriesString.isNotEmpty()) {
                selectedCategoriesString.split(",").toSet()
            } else {
                emptySet()
            }
            VendorsOnBoarding(
                navController = navController,
                selectedCategories = selectedCategories
            )
        }
        composable(NavRoutes.NAV_ROUTE_CALENDER) { CalendarScreen() }
        composable(NavRoutes.NAV_ROUTE_WALLET) { WalletScreen() }
        composable(NavRoutes.NAV_ROUTE_RECOMMENDATIONS) { RecommendationScreen() }
    }
}