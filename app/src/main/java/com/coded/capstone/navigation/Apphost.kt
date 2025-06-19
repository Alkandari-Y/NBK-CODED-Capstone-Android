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
import com.coded.capstone.screens.recommendation.RecommendationScreen

import com.coded.capstone.screens.onboarding.CategoryOnBoarding
import com.coded.capstone.screens.kyc.KycScreen
import com.coded.capstone.screens.onboarding.VendorsOnBoarding
import com.coded.capstone.screens.onboarding.CardSuggestedOnBoarding
import com.coded.capstone.viewModels.HomeScreenViewModel
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
    const val NAV_ROUTE_CARD_SUGGESTION = "card_suggested_onboarding"
    const val NAV_ROUTE_VENDORS_ONBOARDING = "vendors_onboarding"
    const val NAV_ROUTE_CREATE_ACCOUNT = "accounts/create"
    const val NAV_ROUTE_ACCOUNT_DETAILS = "accounts/manage/{accountId}"
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
    val homeScreenViewModel = remember { HomeScreenViewModel(context) }

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
                viewModel = homeScreenViewModel
            )
        }

        composable(NavRoutes.NAV_ROUTE_EDIT_KYC) {
            val kycViewModel = remember { KycViewModel(context) }
            KycScreen(
                navController = navController,
                viewModel= kycViewModel)
        }

        composable(NavRoutes.NAV_ROUTE_CATEGORY_ONBOARDING) {
            CategoryOnBoarding(navController = navController)
        }
        composable(NavRoutes.NAV_ROUTE_VENDORS_ONBOARDING) { backStackEntry ->
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
        composable(NavRoutes.NAV_ROUTE_CARD_SUGGESTION) { backStackEntry ->
            val selectedCategoriesString = backStackEntry.arguments?.getString("selectedCategories") ?: ""
            val selectedVendorsString = backStackEntry.arguments?.getString("selectedVendors") ?: ""

            val selectedCategories = if (selectedCategoriesString.isNotEmpty()) {
                selectedCategoriesString.split(",").toSet()
            } else {
                emptySet()
            }

            val selectedVendors = if (selectedVendorsString.isNotEmpty()) {
                selectedVendorsString.split(",").toSet()
            } else {
                emptySet()
            }

            CardSuggestedOnBoarding(
                navController = navController,
                selectedCategories = selectedCategories,
                selectedVendors = selectedVendors
            )
        }
        composable(NavRoutes.NAV_ROUTE_HOME) {
            val authViewModel = remember { AuthViewModel(context) }
            MainScaffoldWithTabs(navController = navController,authViewModel)
        }
//        composable(NavRoutes.NAV_ROUTE_ACCOUNT_VIEW_ALL) {
//            AllAccountsScreen(
//                navController = navController,
//                viewModel = homeScreenViewModel,
//                onBackClick = {
//                    navController.popBackStack()
//                },
//                onAccountClick = { accountNum: String ->
//                    navController.navigate(NavRoutes.accountDetailRoute(accountNum))
//                },
//            )
//        }

//        composable(NavRoutes.NAV_ROUTE_ACCOUNT_DETAILS) { backStackEntry ->
//            val accountId = backStackEntry.arguments?.getString("accountId")
//            if (accountId != null) {
//                AccountDetailsScreen(
//                    onBackClick = { navController.popBackStack() },
////                    onTransferClick = { navController.navigate(NavRoutes.NAV_ROUTE_TRANSFER) },
//                    viewModel = homeScreenViewModel,
//                    accountId = accountId
//                )
//            }
//        }
        composable(NavRoutes.NAV_ROUTE_CALENDER) { CalendarScreen() }
        composable(NavRoutes.NAV_ROUTE_WALLET) { WalletScreen() }
        composable(NavRoutes.NAV_ROUTE_RECOMMENDATIONS) { RecommendationScreen() }
    }
}