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
import com.coded.capstone.Screens.Wallet.WalletScreen
import com.coded.capstone.Screens.notifications.NotificationScreen
import com.coded.capstone.Screens.onBoarding.CardSuggestedOnBoarding
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.viewModels.AuthViewModel
import com.coded.capstone.screens.calender.CalendarScreen
import com.coded.capstone.screens.accounts.AccountDetailsScreen
import com.coded.capstone.screens.authentication.LoginScreen
import com.coded.capstone.screens.authentication.SignUpScreen
import com.coded.capstone.screens.recommendation.RecommendationScreen
import com.coded.capstone.screens.onboarding.CategoryOnBoarding
import com.coded.capstone.screens.kyc.KycScreen
import com.coded.capstone.screens.kyc.ProfilePage
import com.coded.capstone.screens.onboarding.VendorsOnBoarding
import com.coded.capstone.screens.transfer.TransferScreen
import com.coded.capstone.screens.wallet.RelatedVendorsScreen
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.KycViewModel
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.screens.xp.XpTierScreen
import com.coded.capstone.Screens.notifications.PromotionDetailPage

object NavRoutes {
    const val NAV_ROUTE_LOGIN = "login"
    const val NAV_ROUTE_SIGNUP = "signup"
    const val NAV_ROUTE_LOADING_DASHBOARD = "loading_dashboard"
    const val NAV_ROUTE_HOME = "home"
    const val NAV_ROUTE_WALLET ="wallet"
    const val NAV_ROUTE_TRANSFER = "transfer"
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
    const val NAV_ROUTE_PROFILE = "/profile"
    const val NAV_ROUTE_XP_HISTORY = "xp_history"
const val NAV_ROUTE_NOTIFICATIONS = "notifications"
    const val NAV_ROUTE_PROMOTION_DETAILS = "promotion/{promotionId}"

    const val NAV_ROUTE_VENDORS = "vendors/{category}"
    const val NAV_ROUTE_RELATED_VENDOR = "vendor/{perkId}/{productId}/{accountId}"

    fun accountDetailRoute(accountId: String) = "accounts/manage/$accountId"
    fun vendorsRoute(category: String) = "vendors/$category"
    fun relatedVendorRoute(perkId: String, productId: String, accountId: String) = "vendor/$perkId/$productId/$accountId"
    fun promotionDetailsRoute(promotionId: Long) = "promotion/$promotionId"
    fun homeWithWalletTab() = "home?tab=1"
}

@Composable
fun AppHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val homeScreenViewModel = remember { HomeScreenViewModel(context) }
    val recommendationViewModel = remember { RecommendationViewModel(context) }
val accountViewModel = remember { AccountViewModel(context) }
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
            CategoryOnBoarding(navController = navController, viewModel = homeScreenViewModel)
        }
        composable(NavRoutes.NAV_ROUTE_VENDORS_ONBOARDING) {
            VendorsOnBoarding(
                navController = navController,
                recommendationViewModel
            )
        }
        composable(NavRoutes.NAV_ROUTE_CARD_SUGGESTION) {


            CardSuggestedOnBoarding(
                navController = navController,
                recommendationViewModel,
                accountViewModel
            )
        }
        composable(
            route = NavRoutes.NAV_ROUTE_HOME + "?tab={tab}",
            arguments = listOf(
                androidx.navigation.navArgument("tab") {
                    type = androidx.navigation.NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val authViewModel = remember { AuthViewModel(context) }
            val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
            MainScaffoldWithTabs(
                navController = navController, 
                authViewModel = authViewModel, 
                homeScreenViewModel = homeScreenViewModel,
                initialTab = initialTab
            )
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

        composable(NavRoutes.NAV_ROUTE_ACCOUNT_DETAILS) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId")
            if (accountId != null) {
                AccountDetailsScreen(
                    accountId= accountId,
                    viewModel = homeScreenViewModel,

                    onBack= { navController.popBackStack()})
            }
        }

        composable (NavRoutes.NAV_ROUTE_PROFILE){
            ProfilePage(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.NAV_ROUTE_CALENDER) { CalendarScreen(navController = navController) }

        composable(
            route = NavRoutes.NAV_ROUTE_TRANSFER + "?selectedAccountId={selectedAccountId}",
            arguments = listOf(
                androidx.navigation.navArgument("selectedAccountId") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val selectedAccountId = backStackEntry.arguments?.getString("selectedAccountId")
            TransferScreen(
                navController = navController,
                selectedAccountId = selectedAccountId
            )
        }
        composable(NavRoutes.NAV_ROUTE_RECOMMENDATIONS) { RecommendationScreen(viewModel = homeScreenViewModel) }
        composable(NavRoutes.NAV_ROUTE_XP_HISTORY) {
            XpTierScreen(onBackClick = { navController.popBackStack() })
        }
        composable(NavRoutes.NAV_ROUTE_NOTIFICATIONS) { NotificationScreen(navController = navController) }
        composable(NavRoutes.NAV_ROUTE_PROMOTION_DETAILS) { backStackEntry ->
            val promotionId = backStackEntry.arguments?.getString("promotionId")
            PromotionDetailPage(navController = navController, promotionId = promotionId)
        }
        composable (NavRoutes.NAV_ROUTE_RELATED_VENDOR){ backStackEntry ->
            val perkId = backStackEntry.arguments?.getString("perkId")
            val productId = backStackEntry.arguments?.getString("productId")
            val accountId = backStackEntry.arguments?.getString("accountId")
            if(perkId != null && productId != null && accountId != null){
                RelatedVendorsScreen(
                    navController = navController,
                    perkId = perkId,
                    productId = productId,
                    accountId = accountId,
                    homeViewModel = homeScreenViewModel,
                    recommendationViewModel = recommendationViewModel
                )
            }
        }
    }
}