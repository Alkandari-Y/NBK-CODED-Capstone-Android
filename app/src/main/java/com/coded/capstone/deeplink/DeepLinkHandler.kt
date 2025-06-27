package com.coded.capstone.deeplink

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.coded.capstone.navigation.NavRoutes

/**
 * Handles deep link navigation for the app
 */
object DeepLinkHandler {
    
    /**
     * Process incoming deep link intent
     */
    fun handleDeepLink(intent: Intent?, navController: NavController) {
        val data: Uri? = intent?.data
        if (data != null) {
            processDeepLink(data, navController)
        }
    }
    
    /**
     * Process the deep link URI and navigate accordingly
     */
    private fun processDeepLink(uri: Uri, navController: NavController) {
        when (uri.scheme) {
            "nbkcapstone" -> handleCustomScheme(uri, navController)
            "https" -> handleHttpsScheme(uri, navController)
        }
    }
    
    /**
     * Handle custom scheme deep links (nbkcapstone://)
     */
    private fun handleCustomScheme(uri: Uri, navController: NavController) {
        when (uri.host) {
            "wallet" -> navController.navigate(NavRoutes.NAV_ROUTE_WALLET)
            "transfer" -> navController.navigate(NavRoutes.NAV_ROUTE_TRANSFER)
//            "map" -> navController.navigate(NavRoutes.NAV_ROUTE_MAP)
//            "kyc" -> navController.navigate(NavRoutes.NAV_ROUTE_KYC)
//            "xp" -> navController.navigate(NavRoutes.NAV_ROUTE_XP)
            "calendar" -> navController.navigate(NavRoutes.NAV_ROUTE_CALENDER)
            "recommendations" -> navController.navigate(NavRoutes.NAV_ROUTE_RECOMMENDATIONS)
            "home" -> navController.navigate(NavRoutes.NAV_ROUTE_HOME)
            "login" -> navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
            "signup" -> navController.navigate(NavRoutes.NAV_ROUTE_SIGNUP)
        }
    }
    
    /**
     * Handle HTTPS scheme deep links (https://your-domain.com/...)
     */
    private fun handleHttpsScheme(uri: Uri, navController: NavController) {
        val path = uri.path
        when (path) {
            "/wallet" -> navController.navigate(NavRoutes.NAV_ROUTE_WALLET)
            "/transfer" -> navController.navigate(NavRoutes.NAV_ROUTE_TRANSFER)
//            "/map" -> navController.navigate(NavRoutes.NAV_ROUTE_MAP)
//            "/kyc" -> navController.navigate(NavRoutes.NAV_ROUTE_KYC)
//            "/xp" -> navController.navigate(NavRoutes.NAV_ROUTE_XP)
            "/calendar" -> navController.navigate(NavRoutes.NAV_ROUTE_CALENDER)
            "/recommendations" -> navController.navigate(NavRoutes.NAV_ROUTE_RECOMMENDATIONS)
            "/home" -> navController.navigate(NavRoutes.NAV_ROUTE_HOME)
            "/login" -> navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
            "/signup" -> navController.navigate(NavRoutes.NAV_ROUTE_SIGNUP)
        }
    }
    
    /**
     * Generate deep link URLs for sharing
     */
    object DeepLinkGenerator {
        fun generateWalletLink(): String = "nbkcapstone://wallet"
        fun generateTransferLink(): String = "nbkcapstone://transfer"
        fun generateMapLink(): String = "nbkcapstone://map"
        fun generateKYCLink(): String = "nbkcapstone://kyc"
        fun generateXPLink(): String = "nbkcapstone://xp"
        fun generateCalendarLink(): String = "nbkcapstone://calendar"
        fun generateRecommendationsLink(): String = "nbkcapstone://recommendations"
        fun generateHomeLink(): String = "nbkcapstone://home"
        fun generateLoginLink(): String = "nbkcapstone://login"
        fun generateSignupLink(): String = "nbkcapstone://signup"
    }
} 