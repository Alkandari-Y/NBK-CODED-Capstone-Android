package com.coded.capstone.deeplink

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Example composable showing how to use deep link integration
 */
@Composable
fun DeepLinkUsageExample() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var generatedLink by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Deep Link Integration Examples",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Generate promotion deep link
        Button(
            onClick = {
                scope.launch {
                    generatedLink = DeepLinkUtils.generatePromotionDeepLink("123", context)
                }
            }
        ) {
            Text("Generate Promotion Deep Link")
        }
        
        // Display generated link
        generatedLink?.let { link ->
            Text("Generated: $link")
            
            // Share the generated link
            Button(
                onClick = {
                    DeepLinkUtils.shareDeepLink(
                        context = context,
                        deepLink = link,
                        title = "Check out this promotion!"
                    )
                }
            ) {
                Text("Share Deep Link")
            }
            
            // Test the generated link
            Button(
                onClick = {
                    DeepLinkUtils.testDeepLink(context, link)
                }
            ) {
                Text("Test Deep Link")
            }
        }
        
        Divider()
        
        // Test various deep links
        Text("Test Deep Links", style = MaterialTheme.typography.titleMedium)
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    DeepLinkUtils.testDeepLink(context, "nbkcapstone://wallet")
                }
            ) {
                Text("Wallet")
            }
            
            Button(
                onClick = {
                    DeepLinkUtils.testDeepLink(context, "nbkcapstone://promotion/456")
                }
            ) {
                Text("Promotion")
            }
            
            Button(
                onClick = {
                    DeepLinkUtils.testDeepLink(context, "nbkcapstone://recommendations")
                }
            ) {
                Text("Recommendations")
            }
        }
        
        Divider()
        
        // Test backend integration
        Text("Backend Integration", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = {
                scope.launch {
                    val isValid = DeepLinkUtils.validateDeepLink("nbkcapstone://promotion/123", context)
                    // Handle validation result
                }
            }
        ) {
            Text("Validate Deep Link")
        }
        
        Button(
            onClick = {
                scope.launch {
                    val walletLink = DeepLinkUtils.generateWalletDeepLink(context)
                    // Handle generated link
                }
            }
        ) {
            Text("Generate Wallet Link")
        }
        
        Divider()
        
        // Test parameter parsing
        Text("Parameter Parsing", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = {
                val params = DeepLinkUtils.parseDeepLinkParameters("nbkcapstone://promotion/123?tab=1")
                // Handle parsed parameters
            }
        ) {
            Text("Parse Parameters")
        }
        
        Button(
            onClick = {
                val requiresAuth = DeepLinkUtils.requiresAuthentication("nbkcapstone://wallet")
                // Handle auth requirement
            }
        ) {
            Text("Check Auth Requirement")
        }
    }
}

/**
 * Example of using deep links in a promotion screen
 */
@Composable
fun PromotionScreenExample(promotionId: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Promotion Details",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text("Promotion ID: $promotionId")
        
        // Share this promotion
        Button(
            onClick = {
                scope.launch {
                    val deepLink = DeepLinkUtils.generatePromotionDeepLink(promotionId, context)
                    deepLink?.let { link ->
                        DeepLinkUtils.shareDeepLink(
                            context = context,
                            deepLink = link,
                            title = "Check out this amazing promotion!"
                        )
                    }
                }
            }
        ) {
            Text("Share Promotion")
        }
        
        // Generate deep link for other features
        Button(
            onClick = {
                scope.launch {
                    val walletLink = DeepLinkUtils.generateWalletDeepLink(context)
                    walletLink?.let { link ->
                        DeepLinkUtils.shareDeepLink(
                            context = context,
                            deepLink = link,
                            title = "Check out my wallet!"
                        )
                    }
                }
            }
        ) {
            Text("Share Wallet")
        }
    }
}

/**
 * Example of handling deep links in a notification screen
 */
@Composable
fun NotificationScreenExample() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Test notification deep links
        Button(
            onClick = {
                // Simulate receiving a notification with deep link
                val notificationData = mapOf(
                    "title" to "New Promotion",
                    "body" to "Check out this offer!",
                    "deepLink" to "nbkcapstone://promotion/789",
                    "targetScreen" to "promotion",
                    "parameters" to "{promotionId=789}"
                )
                
                // This would normally be handled by the Firebase service
                // For testing, we can simulate the intent
                val intent = android.content.Intent().apply {
                    putExtra("deepLink", notificationData["deepLink"])
                    putExtra("targetScreen", notificationData["targetScreen"])
                    putExtra("parameters", notificationData["parameters"])
                }
                
                // Process the deep link
                // DeepLinkHandler.handleDeepLink(intent, navController, context)
            }
        ) {
            Text("Simulate Notification Deep Link")
        }
        
        // Test various notification scenarios
        Button(
            onClick = {
                DeepLinkTester.testNotificationDeepLinks(context)
            }
        ) {
            Text("Test Notification Deep Links")
        }
    }
}

/**
 * Example of comprehensive testing
 */
@Composable
fun DeepLinkTestingScreen() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Deep Link Testing",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Run comprehensive tests
        Button(
            onClick = {
                DeepLinkTester.testAllDeepLinks(context)
            }
        ) {
            Text("Run All Tests")
        }
        
        // Test specific functionality
        Button(
            onClick = {
                DeepLinkTester.testParameterParsing()
            }
        ) {
            Text("Test Parameter Parsing")
        }
        
        Button(
            onClick = {
                DeepLinkTester.testAuthenticationRequirements()
            }
        ) {
            Text("Test Auth Requirements")
        }
        
        Button(
            onClick = {
                DeepLinkTester.testDeepLinkSharing(context)
            }
        ) {
            Text("Test Deep Link Sharing")
        }
        
        // Generate test report
        Button(
            onClick = {
                val report = DeepLinkTester.generateTestReport()
                // Display or log the report
            }
        ) {
            Text("Generate Test Report")
        }
    }
}

/**
 * Example of using deep links in a sharing feature
 */
object DeepLinkSharingExample {
    
    /**
     * Share a promotion with friends
     */
    fun sharePromotion(context: Context, promotionId: String, promotionTitle: String) {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
        
        scope.launch {
            val deepLink = DeepLinkUtils.generatePromotionDeepLink(promotionId, context)
            deepLink?.let { link ->
                DeepLinkUtils.shareDeepLink(
                    context = context,
                    deepLink = link,
                    title = "Check out: $promotionTitle"
                )
                
                // Log analytics
                DeepLinkUtils.logDeepLinkAnalytics(
                    deepLink = link,
                    source = "promotion_sharing",
                    context = context
                )
            }
        }
    }
    
    /**
     * Share wallet balance
     */
    fun shareWallet(context: Context) {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
        
        scope.launch {
            val deepLink = DeepLinkUtils.generateWalletDeepLink(context)
            deepLink?.let { link ->
                DeepLinkUtils.shareDeepLink(
                    context = context,
                    deepLink = link,
                    title = "Check out my wallet!"
                )
                
                // Log analytics
                DeepLinkUtils.logDeepLinkAnalytics(
                    deepLink = link,
                    source = "wallet_sharing",
                    context = context
                )
            }
        }
    }
    
    /**
     * Share recommendations
     */
    fun shareRecommendations(context: Context) {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
        
        scope.launch {
            val deepLink = DeepLinkUtils.generateRecommendationsDeepLink(context)
            deepLink?.let { link ->
                DeepLinkUtils.shareDeepLink(
                    context = context,
                    deepLink = link,
                    title = "Check out these recommendations!"
                )
                
                // Log analytics
                DeepLinkUtils.logDeepLinkAnalytics(
                    deepLink = link,
                    source = "recommendations_sharing",
                    context = context
                )
            }
        }
    }
} 