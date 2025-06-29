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
            "nbkcapstone://calendar",
            "nbkcapstone://recommendations",
            "nbkcapstone://home",
            "nbkcapstone://login",
            "nbkcapstone://signup",
            "nbkcapstone://profile",
            "nbkcapstone://xp",
            "nbkcapstone://notifications",
            "nbkcapstone://promotion/123"
        )
        
        deepLinks.forEach { link ->
            testDeepLink(context, link)
        }
    }
    
    /**
     * Test specific deep link types
     */
    fun testWalletDeepLink(context: Context) {
        testDeepLink(context, "nbkcapstone://wallet")
    }
    
    fun testTransferDeepLink(context: Context) {
        testDeepLink(context, "nbkcapstone://transfer")
    }
    
    fun testPromotionDeepLink(context: Context, promotionId: String) {
        testDeepLink(context, "nbkcapstone://promotion/$promotionId")
    }
} 