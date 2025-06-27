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
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.ui.AppBackground

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
    val recommendedCard by recommendationViewModel.recommendedCard.collectAsState()

    // Fetch recommended card when screen is first displayed
    LaunchedEffect(Unit) {
        recommendationViewModel.fetchRecommendedCard()
    }

    // Early return with loading state if no card is available
    if (recommendedCard == null) {
        AppBackground {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        return
    }

    AppBackground {
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

            Text(
                text = "Based on your preferences, we've found the ideal account for you",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 8.dp).padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                fontFamily = RobotoFont
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Card Display - Similar to WalletCard
            SuggestedAccountCard(
                accountProduct = recommendedCard!!,
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
                        text = recommendedCard?.name ?: "Recommended Account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        fontFamily = RobotoFont
                    )
                    
                    Text(
                        text = "${recommendedCard?.accountType?.uppercase()} ACCOUNT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 4.dp),
                        fontFamily = RobotoFont,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = recommendedCard?.description ?: "A personalized account recommendation based on your preferences and banking needs.",
                        fontSize = 16.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 24.sp,
                        fontFamily = RobotoFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Key Features Section
            if (!recommendedCard?.perks.isNullOrEmpty()) {
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

                        recommendedCard?.perks?.forEach { perk ->
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
                    recommendedCard?.interestRate?.let { rate ->
                        AccountDetailRow(
                            icon = Icons.Default.TrendingUp,
                            label = "Interest Rate",
                            value = "${String.format("%.2f", rate)}% APY"
                        )
                    }

                    // Minimum Balance
                    recommendedCard?.minBalanceRequired?.let { minBalance ->
                        AccountDetailRow(
                            icon = Icons.Default.AccountBalance,
                            label = "Minimum Balance",
                            value = "${String.format("%.0f", minBalance)} KD"
                        )
                    }

                    // Credit Limit (for credit accounts)
                    if (recommendedCard?.accountType?.lowercase() == "credit") {
                        recommendedCard?.creditLimit?.let { creditLimit ->
                            AccountDetailRow(
                                icon = Icons.Default.CreditCard,
                                label = "Credit Limit",
                                value = "${String.format("%.0f", creditLimit)} KD"
                            )
                        }
                    }

                    // Annual Fee
                    recommendedCard?.annualFee?.let { fee ->
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
                    recommendedCard?.id?.let { cardId ->
                        accountViewModel.createAccount(cardId)
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

@Composable
private fun SuggestedAccountCard(
    accountProduct: com.coded.capstone.data.responses.accountProduct.AccountProductResponse,
    modifier: Modifier = Modifier
) {
    // Use the same gradient logic as WalletCard
    val cardGradient = when (accountProduct.accountType?.lowercase()) {
        "debit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF132138),
                Color(0xFF263D64),
                Color(0xFF0A121F),
                Color(0xFF132138)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "credit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF16191A),
                Color(0xFF343A3B),
                Color(0xFF000000),
                Color(0xFF16191A)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "savings" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4E5454),
                Color(0xFF818A8A),
                Color(0xFF2F3333),
                Color(0xFF4E5454)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF384349),
                Color(0xFF58656C),
                Color(0xFF273034),
                Color(0xFF384349)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
    }

    Card(
        modifier = modifier
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

@Composable
private fun AccountDetailRow(
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