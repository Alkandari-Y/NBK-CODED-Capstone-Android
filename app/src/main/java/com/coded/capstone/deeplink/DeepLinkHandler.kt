package com.coded.capstone.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.providers.DeepLinkServiceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles deep link navigation for the app with backend integration
 */
object DeepLinkHandler {
    
    private const val TAG = "DeepLinkHandler"
    
    /**
     * Process incoming deep link intent
     */
    fun handleDeepLink(intent: Intent?, navController: NavController, context: Context) {
        val data: Uri? = intent?.data
        if (data != null) {
            processDeepLink(data, navController, context)
        }
        
        // Check for deep link extras from notification
        val deepLink = intent?.getStringExtra("deepLink")
        val targetScreen = intent?.getStringExtra("targetScreen")
        val parameters = intent?.getStringExtra("parameters")
        
        if (deepLink != null && targetScreen != null) {
            handleNotificationDeepLink(deepLink, targetScreen, parameters, navController, context)
        }
    }
    
    /**
     * Process the deep link URI and navigate accordingly
     */
    private fun processDeepLink(uri: Uri, navController: NavController, context: Context) {
        when (uri.scheme) {
            "nbkcapstone" -> handleCustomScheme(uri, navController, context)
            else -> {
                // Try to process with backend for other schemes
                CoroutineScope(Dispatchers.Main).launch {
                    processWithBackend(uri.toString(), navController, context)
                }
            }
        }
    }
    
    /**
     * Handle deep links from notifications
     */
    private fun handleNotificationDeepLink(
        deepLink: String,
        targetScreen: String,
        parameters: String?,
        navController: NavController,
        context: Context
    ) {
        Log.d(TAG, "Handling notification deep link: $deepLink, target: $targetScreen")
        
        when (targetScreen) {
            "promotion" -> {
                val promotionId = extractPromotionId(parameters)
                if (promotionId != null) {
                    navigateToPromotion(promotionId, navController, context)
                }
            }
            else -> {
                // Process with backend for other screens
                CoroutineScope(Dispatchers.Main).launch {
                    processWithBackend(deepLink, navController, context)
                }
            }
        }
    }
    
    /**
     * Process deep link with backend service
     */
    private suspend fun processWithBackend(
        deepLink: String,
        navController: NavController,
        context: Context
    ) {
        try {
            val deepLinkService = DeepLinkServiceProvider(context)
            val result = deepLinkService.processDeepLink(deepLink)
            result.onSuccess { response ->
                Log.d(TAG, "Backend processed deep link: ${response.targetScreen}")
                
                if (response.requiresAuth && !DeepLinkUtils.isUserAuthenticated(context)) {
                    // Store intended destination and redirect to login
                    DeepLinkUtils.storePendingDeepLink(context, deepLink)
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    // Navigate to target screen
                    navigateToTargetScreen(response.targetScreen, response.parameters, navController, context)
                }
            }.onFailure { exception ->
                Log.e(TAG, "Failed to process deep link with backend", exception)
                // Fallback to local processing
                handleLocalDeepLink(deepLink, navController, context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception processing deep link with backend", e)
            // Fallback to local processing
            handleLocalDeepLink(deepLink, navController, context)
        }
    }
    
    /**
     * Handle deep link locally (fallback)
     */
    private fun handleLocalDeepLink(
        deepLink: String,
        navController: NavController,
        context: Context
    ) {
        try {
            val uri = Uri.parse(deepLink)
            if (uri.scheme == "nbkcapstone") {
                handleCustomScheme(uri, navController, context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse deep link: $deepLink", e)
        }
    }
    
    /**
     * Handle custom scheme deep links (nbkcapstone://)
     */
    private fun handleCustomScheme(uri: Uri, navController: NavController, context: Context) {
        val targetScreen = uri.host
        val isAuthenticated = DeepLinkUtils.isUserAuthenticated(context)
        
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
                    DeepLinkUtils.storePendingDeepLink(context, uri.toString())
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            
            // Default to home (auth required)
            "home" -> {
                if (isAuthenticated) {
                    navController.navigate(NavRoutes.NAV_ROUTE_HOME)
                } else {
                    DeepLinkUtils.storePendingDeepLink(context, uri.toString())
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            
            else -> {
                if (isAuthenticated) {
                    navController.navigate(NavRoutes.NAV_ROUTE_HOME)
                } else {
                    DeepLinkUtils.storePendingDeepLink(context, uri.toString())
                    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
    
    /**
     * Navigate to protected screens (user is authenticated)
     */
    private fun navigateToProtectedScreen(uri: Uri, navController: NavController) {
        when (uri.host) {
            "wallet" -> navController.navigate("${NavRoutes.NAV_ROUTE_HOME}?tab=1")
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
                    try {
                        navController.navigate(NavRoutes.promotionDetailsRoute(promotionId.toLong()))
                    } catch (e: NumberFormatException) {
                        Log.e(TAG, "Invalid promotion ID: $promotionId")
                    }
                }
            }
        }
    }
    
    /**
     * Navigate to target screen based on backend response
     */
    private fun navigateToTargetScreen(
        targetScreen: String,
        parameters: Map<String, String>,
        navController: NavController,
        context: Context
    ) {
        when (targetScreen) {
            "wallet" -> navController.navigate("${NavRoutes.NAV_ROUTE_HOME}?tab=1")
            "transfer" -> navController.navigate(NavRoutes.NAV_ROUTE_TRANSFER)
            "calendar" -> navController.navigate(NavRoutes.NAV_ROUTE_CALENDER)
            "recommendations" -> navController.navigate(NavRoutes.NAV_ROUTE_RECOMMENDATIONS)
            "home" -> navController.navigate(NavRoutes.NAV_ROUTE_HOME)
            "profile" -> navController.navigate(NavRoutes.NAV_ROUTE_PROFILE)
            "xp" -> navController.navigate(NavRoutes.NAV_ROUTE_XP_HISTORY)
            "notifications" -> navController.navigate(NavRoutes.NAV_ROUTE_NOTIFICATIONS)
            "promotion" -> {
                val promotionId = parameters["promotionId"]
                if (promotionId != null) {
                    navigateToPromotion(promotionId, navController, context)
                }
            }
            "login" -> navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
            "signup" -> navController.navigate(NavRoutes.NAV_ROUTE_SIGNUP)
            else -> navController.navigate(NavRoutes.NAV_ROUTE_HOME)
        }
    }
    
    /**
     * Navigate to promotion details
     */
    private fun navigateToPromotion(
        promotionId: String,
        navController: NavController,
        context: Context
    ) {
        try {
            val id = promotionId.toLong()
            navController.navigate(NavRoutes.promotionDetailsRoute(id))
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Invalid promotion ID: $promotionId")
        }
    }
    
    /**
     * Extract promotion ID from parameters string
     */
    private fun extractPromotionId(parameters: String?): String? {
        if (parameters.isNullOrEmpty()) return null
        
        return try {
            // Parse parameters like "{promotionId=123}"
            val cleanParams = parameters.removeSurrounding("{", "}")
            val keyValuePairs = cleanParams.split(",")
            
            for (pair in keyValuePairs) {
                val (key, value) = pair.split("=")
                if (key.trim() == "promotionId") {
                    return value.trim()
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract promotion ID from parameters: $parameters", e)
            null
        }
    }
    
    /**
     * Store the intended destination for post-login redirect
     */
    private fun storeIntendedDestination(deepLink: String, context: Context) {
        val prefs = context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("intended_destination", deepLink).apply()
        Log.d(TAG, "Stored intended destination: $deepLink")
    }
    
    /**
     * Get and clear the intended destination
     */
    fun getAndClearIntendedDestination(context: Context): String? {
        val prefs = context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
        val destination = prefs.getString("intended_destination", null)
        if (destination != null) {
            prefs.edit().remove("intended_destination").apply()
            Log.d(TAG, "Retrieved intended destination: $destination")
        }
        return destination
    }
    
    /**
     * Check for pending deep links from notifications
     */
    fun checkPendingDeepLinks(navController: NavController, context: Context) {
        val prefs = context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
        val pendingDeepLink = prefs.getString("pending_deeplink", null)
        val pendingTargetScreen = prefs.getString("pending_target_screen", null)
        val pendingParameters = prefs.getString("pending_parameters", null)
        val timestamp = prefs.getLong("pending_timestamp", 0)
        
        // Only process if less than 5 minutes old
        if (pendingDeepLink != null && pendingTargetScreen != null && 
            System.currentTimeMillis() - timestamp < 5 * 60 * 1000) {
            
            // Clear pending deep link
            prefs.edit().remove("pending_deeplink")
                .remove("pending_target_screen")
                .remove("pending_parameters")
                .remove("pending_timestamp")
                .apply()
            
            // Process the pending deep link
            handleNotificationDeepLink(pendingDeepLink, pendingTargetScreen, pendingParameters, navController, context)
        }
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
        
        /**
         * Generate deep link with backend service
         */
        suspend fun generateDeepLinkWithBackend(
            targetScreen: String,
            parameters: Map<String, String>? = null,
            context: Context
        ): String? {
            return try {
                val deepLinkService = DeepLinkServiceProvider(context)
                val result = deepLinkService.generateDeepLink(targetScreen, parameters)
                result.getOrNull()?.deepLink
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate deep link with backend", e)
                null
            }
        }
    }
} 