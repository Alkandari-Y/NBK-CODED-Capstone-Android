package com.coded.capstone.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Utility class for testing deep links during development
 */
object DeepLinkTester {
    
    /**
     * Test a deep link by launching it
     */
    fun testDeepLink(context: Context, deepLink: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Toast.makeText(context, "Testing: $deepLink", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Test all available deep links
     */
    fun testAllDeepLinks(context: Context) {
        val deepLinks = listOf(
            "nbkcapstone://wallet",
            "nbkcapstone://transfer",
            "nbkcapstone://map",
            "nbkcapstone://kyc",
            "nbkcapstone://xp",
            "nbkcapstone://calendar",
            "nbkcapstone://recommendations",
            "nbkcapstone://home",
            "nbkcapstone://login",
            "nbkcapstone://signup"
        )
        
        deepLinks.forEach { link ->
            testDeepLink(context, link)
        }
    }
    
    /**
     * Test HTTPS deep links (replace with your actual domain)
     */
    fun testHttpsDeepLinks(context: Context) {
        val domain = "your-domain.com"
        val deepLinks = listOf(
            "https://$domain/wallet",
            "https://$domain/transfer",
            "https://$domain/map",
            "https://$domain/kyc",
            "https://$domain/xp",
            "https://$domain/calendar",
            "https://$domain/recommendations",
            "https://$domain/home",
            "https://$domain/login",
            "https://$domain/signup"
        )
        
        deepLinks.forEach { link ->
            testDeepLink(context, link)
        }
    }
} 