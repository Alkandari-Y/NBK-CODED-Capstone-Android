package com.coded.capstone.screens.testing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.coded.capstone.deeplink.DeepLinkTester
import com.coded.capstone.deeplink.DeepLinkUtils
import com.coded.capstone.deeplink.DeepLinkHandler
import kotlinx.coroutines.launch

@Composable
fun DeepLinkTestingScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var testResults by remember { mutableStateOf("") }
    var generatedLink by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Deep Link Testing",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Basic Deep Link Tests
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Basic Deep Links",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { DeepLinkUtils.testDeepLink(context, "nbkcapstone://wallet") }
                    ) {
                        Text("Wallet")
                    }
                    Button(
                        onClick = { DeepLinkUtils.testDeepLink(context, "nbkcapstone://home") }
                    ) {
                        Text("Home")
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { DeepLinkUtils.testDeepLink(context, "nbkcapstone://promotion/123") }
                    ) {
                        Text("Promotion")
                    }
                    Button(
                        onClick = { DeepLinkUtils.testDeepLink(context, "nbkcapstone://recommendations") }
                    ) {
                        Text("Recommendations")
                    }
                }
            }
        }
        
        // Backend Integration Tests
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Backend Integration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            val isValid = DeepLinkUtils.validateDeepLink("nbkcapstone://promotion/123", context)
                            testResults = "Validation result: $isValid"
                        }
                    }
                ) {
                    Text("Validate Deep Link")
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            generatedLink = DeepLinkUtils.generatePromotionDeepLink("123", context)
                            testResults = "Generated: $generatedLink"
                        }
                    }
                ) {
                    Text("Generate Promotion Link")
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            val walletLink = DeepLinkUtils.generateWalletDeepLink(context)
                            testResults = "Wallet link: $walletLink"
                        }
                    }
                ) {
                    Text("Generate Wallet Link")
                }
            }
        }
        
        // Comprehensive Testing
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Comprehensive Tests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        DeepLinkTester.testAllDeepLinks(context)
                        testResults = "All tests completed - check logs"
                    }
                ) {
                    Text("Run All Tests")
                }
                
                Button(
                    onClick = {
                        DeepLinkTester.testParameterParsing()
                        testResults = "Parameter parsing completed - check logs"
                    }
                ) {
                    Text("Test Parameter Parsing")
                }
                
                Button(
                    onClick = {
                        DeepLinkTester.testAuthenticationRequirements()
                        testResults = "Auth requirements completed - check logs"
                    }
                ) {
                    Text("Test Auth Requirements")
                }
                
                Button(
                    onClick = {
                        DeepLinkTester.testDeepLinkSharing(context)
                        testResults = "Sharing tests completed"
                    }
                ) {
                    Text("Test Deep Link Sharing")
                }
            }
        }
        
        // Notification Testing
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Notification Deep Links",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        // Simulate notification deep link
                        val intent = android.content.Intent().apply {
                            putExtra("deepLink", "nbkcapstone://promotion/789")
                            putExtra("targetScreen", "promotion")
                            putExtra("parameters", "{promotionId=789}")
                        }
                        DeepLinkHandler.handleDeepLink(intent, navController, context)
                        testResults = "Notification deep link processed"
                    }
                ) {
                    Text("Simulate Notification Deep Link")
                }
                
                Button(
                    onClick = {
                        DeepLinkTester.testNotificationDeepLinks(context)
                        testResults = "Notification tests completed - check logs"
                    }
                ) {
                    Text("Test Notification Deep Links")
                }
            }
        }
        
        // Authentication Flow Testing
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Authentication Flow",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        // Test protected screen without auth
                        DeepLinkUtils.testDeepLink(context, "nbkcapstone://wallet")
                        testResults = "Testing protected screen access"
                    }
                ) {
                    Text("Test Protected Screen (No Auth)")
                }
                
                Button(
                    onClick = {
                        // Test public screen
                        DeepLinkUtils.testDeepLink(context, "nbkcapstone://login")
                        testResults = "Testing public screen access"
                    }
                ) {
                    Text("Test Public Screen (Login)")
                }
            }
        }
        
        // Results Display
        if (testResults.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Test Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = testResults)
                }
            }
        }
        
        // Generated Link Display
        generatedLink?.let { link ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Generated Deep Link",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = link)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { DeepLinkUtils.shareDeepLink(context, link, "Check this out!") }
                        ) {
                            Text("Share")
                        }
                        Button(
                            onClick = { DeepLinkUtils.testDeepLink(context, link) }
                        ) {
                            Text("Test")
                        }
                    }
                }
            }
        }
    }
} 