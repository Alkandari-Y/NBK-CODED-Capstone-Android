package com.coded.capstone.navigvation

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


object NavRoutes {
    const val NAV_ROUTE_LOGIN = "login"
    const val NAV_ROUTE_SIGNUP = "signup"
    const val NAV_ROUTE_LOADING_DASHBOARD = "loading_dashboard"

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


    LaunchedEffect(Unit){
        if (
            TokenManager.getToken(context) != null &&
            TokenManager.isRememberMeEnabled(context) &&
            !TokenManager.isAccessTokenExpired(context)
        )
        {
            println("navigate to home")
//            navController.navigate(TODO())
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoutes.NAV_ROUTE_LOGIN
    ) {
//
//        composable(NavRoutes.NAV_ROUTE_LOGIN) {
//            val authViewModel = remember { AuthViewModel(context) }
//
//            LoginScreen(
//                navController = navController,
//                onForgotPasswordClick = {
//                    navController.navigate(NavRoutes.NAV_ROUTE_FORGOT_PASSWORD)
//                },
//                viewModel = authViewModel
//            )
//        }
//
//        composable(NavRoutes.NAV_ROUTE_SIGNUP) {
//            val authViewModel = remember { AuthViewModel(context) }
//            SignUpScreen(
//                navController = navController,
//                viewModel = authViewModel
//            )
//        }
    }
}
