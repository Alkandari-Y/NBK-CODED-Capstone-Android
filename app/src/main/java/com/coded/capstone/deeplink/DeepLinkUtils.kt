package com.coded.capstone.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.providers.DeepLinkServiceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class for deep link operations
 */
object DeepLinkUtils {
    
    private const val PENDING_DEEP_LINK_KEY = "pending_deep_link"
    private const val TAG = "DeepLinkUtils"
    
    /**
     * Share a deep link via other apps
     */
    fun shareDeepLink(context: Context, deepLink: String, title: String = "Check this out!") {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, deepLink)
                putExtra(Intent.EXTRA_SUBJECT, title)
            }
            
            val chooser = Intent.createChooser(shareIntent, "Share via")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share deep link", e)
        }
    }
    
    /**
     * Test a deep link by opening it
     */
    fun testDeepLink(context: Context, deepLink: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to test deep link: $deepLink", e)
        }
    }
    
    /**
     * Validate deep link format
     */
    suspend fun validateDeepLink(deepLink: String, context: Context): Boolean {
        return try {
            val deepLinkService = DeepLinkServiceProvider(context)
            val result = deepLinkService.validateDeepLink(deepLink)
            result.isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Failed to validate deep link", e)
            false
        }
    }
    
    /**
     * Generate deep link for promotion sharing
     */
    suspend fun generatePromotionDeepLink(promotionId: String, context: Context): String? {
        return try {
            val deepLinkService = DeepLinkServiceProvider(context)
            val result = deepLinkService.generateDeepLink(
                targetScreen = "promotion",
                parameters = mapOf("promotionId" to promotionId)
            )
            result.getOrNull()?.deepLink ?: DeepLinkHandler.DeepLinkGenerator.generatePromotionLink(promotionId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate promotion deep link", e)
            DeepLinkHandler.DeepLinkGenerator.generatePromotionLink(promotionId)
        }
    }
    
    /**
     * Generate deep link for wallet sharing
     */
    suspend fun generateWalletDeepLink(context: Context): String? {
        return try {
            val deepLinkService = DeepLinkServiceProvider(context)
            val result = deepLinkService.generateDeepLink(targetScreen = "wallet")
            result.getOrNull()?.deepLink ?: DeepLinkHandler.DeepLinkGenerator.generateWalletLink()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate wallet deep link", e)
            DeepLinkHandler.DeepLinkGenerator.generateWalletLink()
        }
    }
    
    /**
     * Generate deep link for recommendations sharing
     */
    suspend fun generateRecommendationsDeepLink(context: Context): String? {
        return try {
            val deepLinkService = DeepLinkServiceProvider(context)
            val result = deepLinkService.generateDeepLink(targetScreen = "recommendations")
            result.getOrNull()?.deepLink ?: DeepLinkHandler.DeepLinkGenerator.generateRecommendationsLink()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate recommendations deep link", e)
            DeepLinkHandler.DeepLinkGenerator.generateRecommendationsLink()
        }
    }
    
    /**
     * Parse deep link parameters
     */
    fun parseDeepLinkParameters(deepLink: String): Map<String, String> {
        return try {
            val uri = Uri.parse(deepLink)
            val params = mutableMapOf<String, String>()
            
            // Parse query parameters
            uri.queryParameterNames.forEach { key ->
                uri.getQueryParameter(key)?.let { value ->
                    params[key] = value
                }
            }
            
            // Parse path parameters for known patterns
            when (uri.host) {
                "promotion" -> {
                    uri.lastPathSegment?.let { segment ->
                        params["promotionId"] = segment
                    }
                }
                "vendor" -> {
                    uri.pathSegments.getOrNull(1)?.let { perkId ->
                        params["perkId"] = perkId
                    }
                    uri.pathSegments.getOrNull(2)?.let { productId ->
                        params["productId"] = productId
                    }
                    uri.pathSegments.getOrNull(3)?.let { accountId ->
                        params["accountId"] = accountId
                    }
                }
            }
            
            params
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse deep link parameters", e)
            emptyMap()
        }
    }
    
    /**
     * Check if deep link requires authentication
     */
    fun requiresAuthentication(deepLink: String): Boolean {
        return try {
            val uri = Uri.parse(deepLink)
            when (uri.host) {
                "login", "signup" -> false
                else -> true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check authentication requirement", e)
            true
        }
    }
    
    /**
     * Get deep link target screen
     */
    fun getTargetScreen(deepLink: String): String? {
        return try {
            val uri = Uri.parse(deepLink)
            uri.host
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get target screen", e)
            null
        }
    }
    
    /**
     * Create deep link for sharing with custom parameters
     */
    fun createDeepLink(
        targetScreen: String,
        parameters: Map<String, String> = emptyMap()
    ): String {
        return try {
            val baseUrl = "nbkcapstone://$targetScreen"
            if (parameters.isEmpty()) {
                baseUrl
            } else {
                val queryParams = parameters.entries.joinToString("&") { (key, value) ->
                    "$key=$value"
                }
                "$baseUrl?$queryParams"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create deep link", e)
            "nbkcapstone://home"
        }
    }
    
    /**
     * Log deep link analytics (for tracking purposes)
     */
    fun logDeepLinkAnalytics(
        deepLink: String,
        source: String = "unknown",
        context: Context
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // TODO: Implement analytics logging to your backend
                Log.d(TAG, "Deep link analytics - Link: $deepLink, Source: $source")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to log deep link analytics", e)
            }
        }
    }

    /**
     * Store a pending deep link for later navigation after authentication
     */
    fun storePendingDeepLink(context: Context, deepLink: String) {
        context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
            .edit().putString(PENDING_DEEP_LINK_KEY, deepLink).apply()
        Log.d(TAG, "Stored pending deep link: $deepLink")
    }

    /**
     * Get the stored pending deep link
     */
    fun getPendingDeepLink(context: Context): String? {
        return context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
            .getString(PENDING_DEEP_LINK_KEY, null)
    }

    /**
     * Clear the stored pending deep link
     */
    fun clearPendingDeepLink(context: Context) {
        context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
            .edit().remove(PENDING_DEEP_LINK_KEY).apply()
        Log.d(TAG, "Cleared pending deep link")
    }

    /**
     * Check if user is authenticated
     */
    fun isUserAuthenticated(context: Context): Boolean {
        val token = TokenManager.getToken(context)
        return token != null && !TokenManager.isAccessTokenExpired(context)
    }

    /**
     * Process pending deep link after authentication
     */
    fun processPendingDeepLink(context: Context, navController: NavController) {
        val pendingDeepLink = getPendingDeepLink(context)
        if (pendingDeepLink != null) {
            Log.d(TAG, "Processing pending deep link: $pendingDeepLink")
            clearPendingDeepLink(context)
            
            try {
                // Create intent from the stored deep link
                val intent = Intent().apply {
                    data = Uri.parse(pendingDeepLink)
                }
                
                // Handle the deep link
                DeepLinkHandler.handleDeepLink(intent, navController, context)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing pending deep link: $pendingDeepLink", e)
                // Fallback to home navigation
                try {
                    navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                } catch (navException: Exception) {
                    Log.e(TAG, "Error navigating to home after deep link failure", navException)
                }
            }
        } else {
            Log.d(TAG, "No pending deep link found, navigating to home")
            try {
                navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to home", e)
            }
        }
    }
} 