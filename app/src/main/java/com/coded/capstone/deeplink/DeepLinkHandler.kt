package com.coded.capstone.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.managers.TokenManager

/**
 * Handles deep link navigation for the app
 */
object DeepLinkHandler {
    
    /**
     * Process incoming deep link intent
     */
    fun handleDeepLink(intent: Intent?, navController: NavController, context: Context) {
        val data: Uri? = intent?.data
        if (data != null) {
            processDeepLink(data, navController, context)
        }
    }
    
    /**
     * Process the deep link URI and navigate accordingly
     */
    private fun processDeepLink(uri: Uri, navController: NavController, context: Context) {
        when (uri.scheme) {
            "nbkcapstone" -> handleCustomScheme(uri, navController, context)
        }
    }
    
    /**
     * Check if user is authenticated
     */
    private fun isUserAuthenticated(context: Context): Boolean {
        return TokenManager.getToken(context) != null && 
               !TokenManager.isAccessTokenExpired(context)
    }
    
    /**
     * Handle custom scheme deep links (nbkcapstone://)
     */
    private fun handleCustomScheme(uri: Uri, navController: NavController, context: Context) {
        val targetScreen = uri.host
        val isAuthenticated = isUserAuthenticated(context)
        
        when (targetScreen) {
            // Public screens (no auth required)
            "login" -> navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
            "signup" -> navController.navigate(NavRoutes.NAV_ROUTE_SIGNUP)
            
            // Protected screens (auth required)
            "wallet", "transfer", "calendar", "recommendations", 
            "profile", "xp", "notifications", "promotion" -> {
                if (isAuthenticated) {
                    navigateToProtectedScreen(uri, navController)
                } else {
                    // Store the intended destination and redirect to login
                    storeIntendedDestination(uri.toString(), context)
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
                }
            }
            
            // Default to home (auth required)
            "home" -> {
                if (isAuthenticated) {
                    navController.navigate(NavRoutes.NAV_ROUTE_HOME)
                } else {
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
                }
            }
            
            else -> {
                if (isAuthenticated) {
                    navController.navigate(NavRoutes.NAV_ROUTE_HOME)
                } else {
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
                }
            }
        }
    }
    
    /**
     * Navigate to protected screens (user is authenticated)
     */
    private fun navigateToProtectedScreen(uri: Uri, navController: NavController) {
        when (uri.host) {
            "wallet" -> navController.navigate(NavRoutes.NAV_ROUTE_WALLET)
            "transfer" -> navController.navigate(NavRoutes.NAV_ROUTE_TRANSFER)
            "calendar" -> navController.navigate(NavRoutes.NAV_ROUTE_CALENDER)
            "recommendations" -> navController.navigate(NavRoutes.NAV_ROUTE_RECOMMENDATIONS)
            "home" -> navController.navigate(NavRoutes.NAV_ROUTE_HOME)
            "profile" -> navController.navigate(NavRoutes.NAV_ROUTE_PROFILE)
            "xp" -> navController.navigate(NavRoutes.NAV_ROUTE_XP_HISTORY)
            "notifications" -> navController.navigate(NavRoutes.NAV_ROUTE_NOTIFICATIONS)
            "promotion" -> {
                val promotionId = uri.lastPathSegment
                if (promotionId != null) {
                    navController.navigate(NavRoutes.promotionDetailsRoute(promotionId.toLong()))
                }
            }
        }
    }
    
    /**
     * Store the intended destination for post-login redirect
     */
    private fun storeIntendedDestination(deepLink: String, context: Context) {
        val prefs = context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("intended_destination", deepLink).apply()
    }
    
    /**
     * Get and clear the intended destination
     */
    fun getAndClearIntendedDestination(context: Context): String? {
        val prefs = context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
        val destination = prefs.getString("intended_destination", null)
        if (destination != null) {
            prefs.edit().remove("intended_destination").apply()
        }
        return destination
    }
    
    /**
     * Generate deep link URLs for sharing
     */
    object DeepLinkGenerator {
        fun generateWalletLink(): String = "nbkcapstone://wallet"
        fun generateTransferLink(): String = "nbkcapstone://transfer"
        fun generateCalendarLink(): String = "nbkcapstone://calendar"
        fun generateRecommendationsLink(): String = "nbkcapstone://recommendations"
        fun generateHomeLink(): String = "nbkcapstone://home"
        fun generateLoginLink(): String = "nbkcapstone://login"
        fun generateSignupLink(): String = "nbkcapstone://signup"
        fun generateProfileLink(): String = "nbkcapstone://profile"
        fun generateXPLink(): String = "nbkcapstone://xp"
        fun generateNotificationsLink(): String = "nbkcapstone://notifications"
        fun generatePromotionLink(promotionId: String): String = "nbkcapstone://promotion/$promotionId"
    }
} 