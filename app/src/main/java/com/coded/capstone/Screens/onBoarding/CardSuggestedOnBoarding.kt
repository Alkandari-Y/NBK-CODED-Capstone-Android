package com.coded.capstone.Screens.onBoarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.OnBoardingAccountUiState
import com.coded.capstone.viewModels.RecommendationViewModel
import kotlinx.coroutines.delay

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
    var showContent by remember { mutableStateOf(false) }
    val recommendedCard by recommendationViewModel.recommendedCard.collectAsState()

    // Monitor account creation state
    val onBoardingAccountUiState by accountViewModel.onBoardingAccountUiState.collectAsState()
    var isCreatingAccount by remember { mutableStateOf(false) }

    // Sparkle animation states
    val sparkleAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(1000, easing = EaseInOut),
        label = "sparkle_fade"
    )

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
            // Show loading for 2 seconds, then show content
            delay(2000)
            isLoading = false
            errorMessage = null
            delay(100)
            showContent = true
            println("CardSuggestedOnBoarding: Card loaded successfully: ${recommendedCard?.name}")
        } else {
            // Give it some time to load, then show error if still null
            delay(5000) // Wait 5 seconds
            if (recommendedCard == null) {
                isLoading = false
                errorMessage = "No recommended card available. Please check your network connection."
                println("CardSuggestedOnBoarding: Timeout - no card received after 5 seconds")
            }
        }
    }

    // Handle account creation completion
    LaunchedEffect(onBoardingAccountUiState) {
        when (val state = onBoardingAccountUiState) {
            is OnBoardingAccountUiState.Success -> {
                isCreatingAccount = false
                // Navigate to home screen and pass refresh flag
                navController.navigate("${NavRoutes.NAV_ROUTE_HOME}?refreshAccounts=true") {
                    popUpTo(0)
                }
            }
            is OnBoardingAccountUiState.Error -> {
                isCreatingAccount = false
                errorMessage = "Failed to create account: ${state.message}"
            }
            is OnBoardingAccountUiState.Loading -> {
                isCreatingAccount = true
            }
            else -> { /* Idle state */ }
        }
    }

    // Function to determine recommendation type based on product (updated with real categories)
    fun getRecommendationType(product: AccountProductResponse): String? {
        return when {
            product.name?.lowercase()?.contains("retail") == true -> "retail"
            product.name?.lowercase()?.contains("travel") == true -> "travel"
            product.name?.lowercase()?.contains("dining") == true -> "dining"
            product.name?.lowercase()?.contains("fashion") == true -> "fashion"
            product.name?.lowercase()?.contains("technology") == true -> "technology"
            product.name?.lowercase()?.contains("hospitality") == true -> "hospitality"
            product.name?.lowercase()?.contains("education") == true -> "education"
            product.name?.lowercase()?.contains("entertainment") == true -> "entertainment"
            product.name?.lowercase()?.contains("personal care") == true -> "personal care"
            product.name?.lowercase()?.contains("wholesale") == true -> "wholesale"
            // Fallback based on account type
            product.accountType?.lowercase() == "credit" -> "retail"
            product.accountType?.lowercase() == "savings" -> "hospitality"
            product.accountType?.lowercase() == "debit" -> "travel"
            else -> "retail" // Default recommendation type
        }
    }

    // Loading Screen
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated loading indicator
                val infiniteTransition = rememberInfiniteTransition(label = "loading")

                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF8EC5FF),
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 3.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Finding your ideal card...",
                    color = Color(0xFF374151),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoFont
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Analyzing your preferences",
                    color = Color(0xFF6B7280),
                    fontSize = 16.sp,
                    fontFamily = RobotoFont
                )

                // Animated dots
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) { index ->
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = index * 200),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dot_$index"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .alpha(alpha)
                                .background(Color(0xFF8EC5FF), CircleShape)
                        )
                    }
                }
            }
        }
        return
    }

    // Error Screen (keep original logic)
    if (errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
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
                    color = Color(0xFF374151),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoFont
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFF6B7280),
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

    // Main Content
    recommendedCard?.let { card ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151),
                    textAlign = TextAlign.Center,
                    fontFamily = RobotoFont
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Card Display
                SuggestedAccountCard(
                    accountProduct = card,
                    recommendationType = getRecommendationType(card),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card Description - Floating Text
                Text(
                    text = card.description ?: "A personalized account recommendation based on your preferences and banking needs.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    lineHeight = 24.sp,
                    fontFamily = RobotoFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Two-Column Layout: Details & Benefits - FIXED SIZE (swapped order)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp), // Fixed height for both cards
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Details Card - FIXED SIZE (now first/left)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .width(150.dp), // Fixed width
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF23272E) // Dark grey
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            // Header without icon
                            Text(
                                text = "Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = RobotoFont
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fixed content area
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // Takes remaining space
                            ) {
                                // Always show exactly 4 rows, regardless of data
                                val detailRows = mutableListOf<Pair<String, String>>()

                                // Add available data
                                card.interestRate?.let { rate ->
                                    detailRows.add("Interest" to "${String.format("%.2f", rate)}%")
                                }
                                card.minBalanceRequired?.let { minBalance ->
                                    detailRows.add("Min Balance" to "${String.format("%.0f", minBalance)} KD")
                                }
                                if (card.accountType?.lowercase() == "credit") {
                                    card.creditLimit?.let { creditLimit ->
                                        detailRows.add("Credit Limit" to "${String.format("%.0f", creditLimit)} KD")
                                    }
                                }
                                card.annualFee?.let { fee ->
                                    detailRows.add("Annual Fee" to if (fee == 0.0) "Free" else "${String.format("%.0f", fee)} KD")
                                }

                                // Fill with placeholder data if needed to always have 4 rows
                                while (detailRows.size < 4) {
                                    when (detailRows.size) {
                                        0 -> detailRows.add("Interest" to "2.90%")
                                        1 -> detailRows.add("Min Balance" to "0 KD")
                                        2 -> detailRows.add("Credit Limit" to "1200 KD")
                                        3 -> detailRows.add("Annual Fee" to "40 KD")
                                    }
                                }

                                // Display exactly 4 rows
                                detailRows.take(4).forEach { (label, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Unified blue circle bullet point
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color(0xFF8EC5FF), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = label,
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                fontFamily = RobotoFont,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = value,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontFamily = RobotoFont,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Benefits Card - FIXED SIZE (now second/right)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .width(150.dp), // Fixed width
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF23272E) // Dark grey
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            // Header without icon
                            Text(
                                text = "Benefits",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = RobotoFont
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fixed content area with scroll if needed
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // Takes remaining space
                            ) {
                                if (!card.perks.isNullOrEmpty()) {
                                    card.perks.take(3).forEach { perk ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Unified blue circle bullet point
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color(0xFF8EC5FF), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = (perk.type ?: "Special Benefit").take(15), // Limit text length
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                fontFamily = RobotoFont,
                                                maxLines = 1, // Single line only
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                } else {
                                    // Default benefits if none provided
                                    listOf("CASHBACK", "DISCOUNT", "REWARDS").forEach { benefit ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Unified blue circle bullet point
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color(0xFF8EC5FF), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = benefit,
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                fontFamily = RobotoFont,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

                // Floating Reward Statement with Gold Sparkles on BOTH sides
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(sparkleAlpha)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFEAB308), // Gold color
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Earn Cash Back and XP upon application!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF23272E),
                        fontFamily = RobotoFont,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFEAB308), // Gold color
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Apply Now Button
                Button(
                    onClick = {
                        userWillApply = true
                        card.id?.let { cardId ->
                            accountViewModel.onboardingCreateCard(cardId)
                        }
                    },
                    enabled = !isCreatingAccount,
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
                        containerColor = if (isCreatingAccount) Color(0xFF6B7280) else Color(0xFF8EC5FF)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isCreatingAccount) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Creating Account...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = RobotoFont,
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Apply Now",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = RobotoFont,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Continue Banking - Underlined Text
                Text(
                    text = "Continue Banking",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp,
                    fontFamily = RobotoFont,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        userWillApply = false
                        navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                            popUpTo(0)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Progress indicator moved to top, so removing from bottom
                // No progress indicator here anymore

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SuggestedAccountCard(
    accountProduct: AccountProductResponse,
    recommendationType: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        // Card gradient logic (updated with intuitive colors)
        val cardGradient = when {
            // Intuitive colors that match category types
            recommendationType?.lowercase() == "retail" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3B4B6B), // Lighter muted navy - still premium but more visible
                    Color(0xFF4A5A7A),
                    Color(0xFF2F3F5F),
                    Color(0xFF3B4B6B)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "travel" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3F4A56), // Lighter muted charcoal - better for stacks
                    Color(0xFF4B5663),
                    Color(0xFF353E4A),
                    Color(0xFF3F4A56)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "dining" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3D2B1F), // Deep warm brown - restaurant warmth & appetite
                    Color(0xFF4A3529),
                    Color(0xFF2F1F15),
                    Color(0xFF3D2B1F)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "fashion" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4A3B47), // Sophisticated mauve - luxury fashion elegance
                    Color(0xFF564753),
                    Color(0xFF3E2F3B),
                    Color(0xFF4A3B47)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "technology" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3C3F4F), // Lighter muted blue-purple - tech but not gloomy
                    Color(0xFF484B5B),
                    Color(0xFF323543),
                    Color(0xFF3C3F4F)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "hospitality" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0F1419), // Almost black with green undertones - luxury hospitality
                    Color(0xFF1A1F1A),
                    Color(0xFF0D1117),
                    Color(0xFF0F1419)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "education" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3D4C72), // Lighter academic blue - more visible in stacks
                    Color(0xFF495882),
                    Color(0xFF334062),
                    Color(0xFF3D4C72)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "entertainment" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4A2C3A), // Deep wine/burgundy - theater & entertainment elegance
                    Color(0xFF563846),
                    Color(0xFF3E202E),
                    Color(0xFF4A2C3A)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "personal care" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3F4A3C), // Sophisticated sage green - spa & wellness
                    Color(0xFF4B5648),
                    Color(0xFF333E30),
                    Color(0xFF3F4A3C)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            recommendationType?.lowercase() == "wholesale" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4A5568), // Lighter professional gray - better visibility
                    Color(0xFF556175),
                    Color(0xFF3E495B),
                    Color(0xFF4A5568)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            // Regular account type cards (fallback)
            accountProduct.accountType?.lowercase() == "debit" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF132138),
                    Color(0xFF263D64),
                    Color(0xFF0A121F),
                    Color(0xFF132138)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            accountProduct.accountType?.lowercase() == "credit" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF16191A),
                    Color(0xFF343A3B),
                    Color(0xFF000000),
                    Color(0xFF16191A)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            accountProduct.accountType?.lowercase() == "savings" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4E5454),
                    Color(0xFF818A8A),
                    Color(0xFF2F3333),
                    Color(0xFF4E5454)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(350f, 250f)
            )

            accountProduct.accountType?.lowercase() == "business" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3730a3),
                    Color(0xFF6862C7),
                    Color(0xFF201F5B),
                    Color(0xFF3730a3)
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
                // Subtle geometric pattern overlay
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
                    // Top section: Bank name and contactless (FLIPPED)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = (accountProduct.name ?: "ACCOUNT").uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = RobotoFont
                        )

                        // Contactless Icon - Rotated to point right
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Contactless Payment",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .size(28.dp)
                                .rotate(90f) // Rotate 90 degrees to point right
                        )
                    }

                    // Middle section: EMV Chip
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

                    // Bottom section: Card details
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

                // Premium shine effect
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
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unified blue circle bullet point
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF8EC5FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White,
                fontFamily = RobotoFont
            )
        }
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = RobotoFont
        )
    }
}