package com.coded.capstone.deeplink

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class for testing deep link functionality
 */
object DeepLinkTester {
    
    private const val TAG = "DeepLinkTester"
    
    /**
     * Test all deep link scenarios
     */
    fun testAllDeepLinks(context: Context) {
        Log.d(TAG, "Starting comprehensive deep link testing...")
        
        // Test basic deep links
        testBasicDeepLinks(context)
        
        // Test promotion deep links
        testPromotionDeepLinks(context)
        
        // Test backend integration
        testBackendIntegration(context)
        
        // Test notification deep links
        testNotificationDeepLinks(context)
    }
    
    /**
     * Test basic deep link navigation
     */
    private fun testBasicDeepLinks(context: Context) {
        Log.d(TAG, "Testing basic deep links...")
        
        val basicLinks = listOf(
            "nbkcapstone://home",
            "nbkcapstone://wallet",
            "nbkcapstone://transfer",
            "nbkcapstone://calendar",
            "nbkcapstone://recommendations",
            "nbkcapstone://profile",
            "nbkcapstone://xp",
            "nbkcapstone://notifications",
            "nbkcapstone://login",
            "nbkcapstone://signup"
        )
        
        basicLinks.forEach { link ->
            Log.d(TAG, "Testing: $link")
            DeepLinkUtils.testDeepLink(context, link)
        }
    }
    
    /**
     * Test promotion deep links
     */
    private fun testPromotionDeepLinks(context: Context) {
        Log.d(TAG, "Testing promotion deep links...")
        
        val promotionLinks = listOf(
            "nbkcapstone://promotion/123",
            "nbkcapstone://promotion/456",
            "nbkcapstone://promotion/789"
        )
        
        promotionLinks.forEach { link ->
            Log.d(TAG, "Testing promotion link: $link")
            DeepLinkUtils.testDeepLink(context, link)
        }
    }
    
    /**
     * Test backend integration
     */
    private fun testBackendIntegration(context: Context) {
        Log.d(TAG, "Testing backend integration...")
        
        CoroutineScope(Dispatchers.IO).launch {
            // Test deep link validation
            val testLinks = listOf(
                "nbkcapstone://promotion/123",
                "nbkcapstone://wallet",
                "https://example.com/invalid"
            )
            
            testLinks.forEach { link ->
                val isValid = DeepLinkUtils.validateDeepLink(link, context)
                Log.d(TAG, "Validation result for $link: $isValid")
            }
            
            // Test deep link generation
            val promotionLink = DeepLinkUtils.generatePromotionDeepLink("123", context)
            Log.d(TAG, "Generated promotion link: $promotionLink")
            
            val walletLink = DeepLinkUtils.generateWalletDeepLink(context)
            Log.d(TAG, "Generated wallet link: $walletLink")
            
            val recommendationsLink = DeepLinkUtils.generateRecommendationsDeepLink(context)
            Log.d(TAG, "Generated recommendations link: $recommendationsLink")
        }
    }
    
    /**
     * Test notification deep links
     */
    fun testNotificationDeepLinks(context: Context) {
        Log.d(TAG, "Testing notification deep links...")
        
        // Simulate notification deep link data
        val notificationData = mapOf(
            "title" to "Test Promotion",
            "body" to "Check out this amazing offer!",
            "deepLink" to "nbkcapstone://promotion/123",
            "targetScreen" to "promotion",
            "requiresAuth" to "true",
            "parameters" to "{promotionId=123}"
        )
        
        Log.d(TAG, "Simulating notification with data: $notificationData")
        
        // This would normally be handled by the Firebase messaging service
        // For testing, we can simulate the intent
        val intent = android.content.Intent().apply {
            putExtra("deepLink", notificationData["deepLink"])
            putExtra("targetScreen", notificationData["targetScreen"])
            putExtra("parameters", notificationData["parameters"])
        }
        
        // Note: This would need to be called from a context where NavController is available
        Log.d(TAG, "Notification deep link intent created: $intent")
    }
    
    /**
     * Test deep link parameter parsing
     */
    fun testParameterParsing() {
        Log.d(TAG, "Testing parameter parsing...")
        
        val testLinks = listOf(
            "nbkcapstone://promotion/123",
            "nbkcapstone://vendor/456/789/101",
            "nbkcapstone://wallet?tab=1",
            "nbkcapstone://transfer?selectedAccountId=ACC123"
        )
        
        testLinks.forEach { link ->
            val params = DeepLinkUtils.parseDeepLinkParameters(link)
            Log.d(TAG, "Parameters for $link: $params")
        }
    }
    
    /**
     * Test authentication requirements
     */
    fun testAuthenticationRequirements() {
        Log.d(TAG, "Testing authentication requirements...")
        
        val testLinks = listOf(
            "nbkcapstone://login",
            "nbkcapstone://signup",
            "nbkcapstone://wallet",
            "nbkcapstone://promotion/123"
        )
        
        testLinks.forEach { link ->
            val requiresAuth = DeepLinkUtils.requiresAuthentication(link)
            Log.d(TAG, "Authentication required for $link: $requiresAuth")
        }
    }
    
    /**
     * Test deep link sharing
     */
    fun testDeepLinkSharing(context: Context) {
        Log.d(TAG, "Testing deep link sharing...")
        
        val testLinks = listOf(
            "nbkcapstone://promotion/123",
            "nbkcapstone://wallet",
            "nbkcapstone://recommendations"
        )
        
        testLinks.forEach { link ->
            Log.d(TAG, "Testing share for: $link")
            DeepLinkUtils.shareDeepLink(context, link, "Check out this amazing feature!")
        }
    }
    
    /**
     * Generate test report
     */
    fun generateTestReport(): String {
        return """
            Deep Link Test Report
            ====================
            
            Features Tested:
            - Basic deep link navigation
            - Promotion deep links
            - Backend integration
            - Notification deep links
            - Parameter parsing
            - Authentication requirements
            - Deep link sharing
            
            Supported Deep Links:
            - nbkcapstone://home
            - nbkcapstone://wallet
            - nbkcapstone://transfer
            - nbkcapstone://calendar
            - nbkcapstone://recommendations
            - nbkcapstone://profile
            - nbkcapstone://xp
            - nbkcapstone://notifications
            - nbkcapstone://promotion/{id}
            - nbkcapstone://login
            - nbkcapstone://signup
            
            Backend Integration:
            - Deep link validation
            - Deep link generation
            - Parameter processing
            - Authentication checks
            
            Notification Integration:
            - FCM message handling
            - Deep link extraction
            - Navigation routing
            - Parameter parsing
        """.trimIndent()
    }
} 