package com.coded.capstone.Screens.onBoarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.RecommendationViewModel


// Roboto font family
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

@Composable
fun CardSuggestedOnBoarding(
    navController: NavController,
    recommendationViewModel: RecommendationViewModel,
    accountViewModel: AccountViewModel
) {
    var userWillApply by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val recommendedCard by recommendationViewModel.recommendedCard.collectAsState()

    // Fetch recommended card when screen is first displayed
    LaunchedEffect(Unit) {
        println("CardSuggestedOnBoarding: Starting to fetch recommended card")
        try {
            recommendationViewModel.fetchRecommendedCard()
        } catch (e: Exception) {
            println("CardSuggestedOnBoarding: Error in LaunchedEffect: ${e.message}")
            errorMessage = "Failed to load recommendation: ${e.message}"
            isLoading = false
        }
    }

    // Monitor the recommendedCard state changes
    LaunchedEffect(recommendedCard) {
        println("CardSuggestedOnBoarding: recommendedCard state changed: $recommendedCard")
        if (recommendedCard != null) {
            isLoading = false
            errorMessage = null
            println("CardSuggestedOnBoarding: Card loaded successfully: ${recommendedCard?.name}")
        } else {
            // Give it some time to load, then show error if still null
            kotlinx.coroutines.delay(5000) // Wait 5 seconds
            if (recommendedCard == null) {
                isLoading = false
                errorMessage = "No recommended card available. Please check your network connection."
                println("CardSuggestedOnBoarding: Timeout - no card received after 5 seconds")
            }
        }
    }

    // Function to determine recommendation type based on product (same as recommendation screen)
    fun getRecommendationType(product: AccountProductResponse): String? {
        return when {
            product.name?.lowercase()?.contains("travel") == true -> "travel"
            product.name?.lowercase()?.contains("family") == true -> "family essentials"
            product.name?.lowercase()?.contains("entertainment") == true -> "entertainment"
            product.name?.lowercase()?.contains("shopping") == true -> "shopping"
            product.name?.lowercase()?.contains("dining") == true -> "dining"
            product.name?.lowercase()?.contains("health") == true -> "health"
            product.name?.lowercase()?.contains("education") == true -> "education"
            product.accountType?.lowercase() == "credit" -> "shopping"
            product.accountType?.lowercase() == "savings" -> "family essentials"
            product.accountType?.lowercase() == "debit" -> "travel"
            else -> "shopping" // Default recommendation type
        }
    }

    // Show loading state
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF23272E)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color(0xFF8EC5FF))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Finding your perfect card...",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = RobotoFont
                )
            }
        }
        return
    }

    // Show error state
    if (errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF23272E)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Oops! Something went wrong",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoFont
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontFamily = RobotoFont,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        recommendationViewModel.fetchRecommendedCard()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8EC5FF)
                    )
                ) {
                    Text(
                        text = "Try Again",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    // Show content only if we have a recommended card
    recommendedCard?.let { card ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF23272E)) // Navy background like bottom navbar
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Header Section
                Text(
                    text = "Your Perfect Match",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = RobotoFont
                )


                Spacer(modifier = Modifier.height(40.dp))

                // Card Display - Similar to WalletCard
                SuggestedAccountCard(
                    accountProduct = card,
                    recommendationType = getRecommendationType(card),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Account Details Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = card.name ?: "Recommended Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            fontFamily = RobotoFont
                        )
                        
                        Text(
                            text = "${card.accountType?.uppercase()} ACCOUNT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 4.dp),
                            fontFamily = RobotoFont,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = card.description ?: "A personalized account recommendation based on your preferences and banking needs.",
                            fontSize = 16.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 24.sp,
                            fontFamily = RobotoFont
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Key Features Section
                if (!card.perks.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Key Benefits",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    fontFamily = RobotoFont
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            card.perks.forEach { perk ->
                                Row(
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = perk.type ?: "Special Benefit",
                                        fontSize = 16.sp,
                                        color = Color(0xFF374151),
                                        fontFamily = RobotoFont,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Account Specifications
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Account Details",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                fontFamily = RobotoFont
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interest Rate
                        card.interestRate?.let { rate ->
                            AccountDetailRow(
                                icon = Icons.Default.TrendingUp,
                                label = "Interest Rate",
                                value = "${String.format("%.2f", rate)}% APY"
                            )
                        }

                        // Minimum Balance
                        card.minBalanceRequired?.let { minBalance ->
                            AccountDetailRow(
                                icon = Icons.Default.AccountBalance,
                                label = "Minimum Balance",
                                value = "${String.format("%.0f", minBalance)} KD"
                            )
                        }

                        // Credit Limit (for credit accounts)
                        if (card.accountType?.lowercase() == "credit") {
                            card.creditLimit?.let { creditLimit ->
                                AccountDetailRow(
                                    icon = Icons.Default.CreditCard,
                                    label = "Credit Limit",
                                    value = "${String.format("%.0f", creditLimit)} KD"
                                )
                            }
                        }

                        // Annual Fee
                        card.annualFee?.let { fee ->
                            AccountDetailRow(
                                icon = Icons.Default.Payment,
                                label = "Annual Fee",
                                value = if (fee == 0.0) "Free" else "${String.format("%.0f", fee)} KD"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Button(
                    onClick = {
                        userWillApply = true
                        card.id?.let { cardId ->
                            accountViewModel.onboardingCreateCard(cardId)
                        }
                        navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.3f),
                            spotColor = Color(0xFF8EC5FF).copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8EC5FF)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Open This Account",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = RobotoFont,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        userWillApply = false
                        navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue Without Opening Account",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontFamily = RobotoFont
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress indicator
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (index == 2) Color.White else Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        )
                        if (index < 2) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SuggestedAccountCard(
    accountProduct: com.coded.capstone.data.responses.accountProduct.AccountProductResponse,
    recommendationType: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Recommendation text (same as recommendation screen)
        recommendationType?.let { type ->
            Text(
                text = "Recommended for $type",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 16.dp),
                fontFamily = RobotoFont
            )
        }

        // Use the same gradient logic as WalletCard (recommendation type first, then account type)
        val cardGradient = when {
            // Special recommendation cards (same as WalletCard)
            recommendationType?.lowercase() == "travel" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF7AF380), // Blue 800
                    Color(0xFFA5F5A9), // Blue 500
                    Color(0xFF136870), // Blue 700
                    Color(0xFF136870)  // Blue 800
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "family essentials" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF49899F), // Emerald 600
                    Color(0xFF80D1EC), // Emerald 500
                    Color(0xFF115F79), // Emerald 700
                    Color(0xFF115F79)  // Emerald 600
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "entertainment" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF651351), // Violet 600
                    Color(0xFF8D3077), // Violet 500
                    Color(0xFF2C1365), // Violet 700
                    Color(0xFF2C1365)  // Violet 600
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "shopping" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF6B1D45), // Red 600
                    Color(0xFF9F3B70), // Red 500
                    Color(0xFF501233), // Red 700
                    Color(0xFF4F1332)  // Red 600
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "dining" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD97706), // Amber 600
                    Color(0xFFF59E0B), // Amber 500
                    Color(0xFFB45309), // Amber 700
                    Color(0xFFD97706)  // Amber 600
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "health" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF600612), // Cyan 600
                    Color(0xFF861020), // Cyan 500
                    Color(0xFF600612), // Cyan 700
                    Color(0xFF600612)  // Cyan 600
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "education" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF219406), // Orange 800
                    Color(0xFF8CC241), // Orange 600
                    Color(0xFF219406), // Orange 700
                    Color(0xFF219406)  // Orange 800
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )
            // Regular account type cards (fallback when no recommendation type)
            accountProduct.accountType?.lowercase() == "debit" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF132138), // Slate 800
                    Color(0xFF263D64), // Slate 700
                    Color(0xFF0A121F), // Slate 600
                    Color(0xFF132138)  // Slate 500
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            accountProduct.accountType?.lowercase() == "credit" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF16191A), // Red 800
                    Color(0xFF343A3B), // Red 800
                    Color(0xFF000000), // Red 600
                    Color(0xFF16191A)  // Red 500
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            accountProduct.accountType?.lowercase() == "savings" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4E5454), // Green 900
                    Color(0xFF818A8A), // Green 800
                    Color(0xFF2F3333), // Green 600
                    Color(0xFF4E5454)  // Green 500
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            accountProduct.accountType?.lowercase() == "business" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3730a3), // Indigo 900
                    Color(0xFF6862C7), // Indigo 800
                    Color(0xFF201F5B), // Indigo 600
                    Color(0xFF3730a3)  // Indigo 500
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            else -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF384349), // Gray 700
                    Color(0xFF58656C), // Gray 600
                    Color(0xFF273034), // Gray 500
                    Color(0xFF384349)  // Gray 400
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.4f),
                    spotColor = Color.Black.copy(alpha = 0.4f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardGradient)
            ) {
                // Subtle geometric pattern overlay (same as WalletCard)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.03f),
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.02f)
                                ),
                                radius = 400f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top section: Bank name and contactless (same as WalletCard)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = (accountProduct.name ?: "NBK ACCOUNT").uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = RobotoFont
                        )

                        Icon(
                            imageVector = Icons.Default.Contactless,
                            contentDescription = "Contactless Payment",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Middle section: EMV Chip (exact same as WalletCard)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(40.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700), // Gold
                                            Color(0xFFDAA520), // Goldenrod
                                            Color(0xFFB8860B), // Dark goldenrod
                                            Color(0xFFFFD700)  // Gold
                                        )
                                    ),
                                    RoundedCornerShape(6.dp)
                                )
                                .shadow(2.dp, RoundedCornerShape(6.dp))
                        ) {
                            // Chip contact pattern
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(3) { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        repeat(4) { col ->
                                            Box(
                                                modifier = Modifier
                                                    .size(3.dp)
                                                    .background(
                                                        Color(0xFF8B4513).copy(alpha = 0.8f),
                                                        RoundedCornerShape(1.dp)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom section: Card details (same structure as WalletCard)
                    Column {
                        // Card number placeholder
                        Text(
                            text = "•••• •••• •••• NEW",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom row: Card type and status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Account type
                            Column {
                                Text(
                                    text = "ACCOUNT TYPE",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = (accountProduct.accountType ?: "ACCOUNT").uppercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Status
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "STATUS",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "RECOMMENDED",
                                    color = Color(0xFF10B981),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Premium shine effect (same as WalletCard)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(500f, 300f)
                            )
                        )
                )
            }
        }
    }
}
    @Composable
    fun AccountDetailRow(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                fontFamily = RobotoFont,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937),
                fontFamily = RobotoFont
            )
        }
    }
