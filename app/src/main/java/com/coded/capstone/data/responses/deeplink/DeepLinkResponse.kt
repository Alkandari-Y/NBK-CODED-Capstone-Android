package com.coded.capstone.data.responses.deeplink

/**
 * Response DTO for deep link processing
 */
data class DeepLinkResponse(
    /**
     * The target screen to navigate to
     */
    val targetScreen: String,
    
    /**
     * Whether authentication is required for this screen
     */
    val requiresAuth: Boolean,
    
    /**
     * Additional parameters extracted from the deep link
     */
    val parameters: Map<String, String>,
    
    /**
     * Human-readable message about the deep link
     */
    val message: String,
    
    /**
     * Generated deep link URL (for generate endpoint)
     */
    val deepLink: String? = null
) 