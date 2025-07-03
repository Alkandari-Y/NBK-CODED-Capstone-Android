@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.recommendation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.coded.capstone.composables.recommendation.RecommendationCard
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.data.responses.recommendation.RecommendedAccountProducts
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.viewModels.RecommendedProductsUiState
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.AccountCreateUiState
import com.coded.capstone.composables.wallet.WalletCard
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import com.coded.capstone.composables.recommendation.InfoCard
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.zIndex
import com.coded.capstone.R
import com.coded.capstone.SVG.SharpStarsIcon
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.border


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: HomeScreenViewModel,
    onItemClick: (AccountProductResponse) -> Unit = {},
    onActivateClick: (AccountProductResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val accountViewModel = remember { AccountViewModel(context) }
    val recommendationViewModel = remember { RecommendationViewModel(context) }
    val accountCreateState by accountViewModel.accountUiState.collectAsState()
    val shouldNavigate by accountViewModel.shouldNavigate.collectAsState()

    val recommendedProducts by recommendationViewModel.recommendedProducts.collectAsState()
    val recommendedProductsUiState by recommendationViewModel.recommendedProductsUiState.collectAsState()
    val accountsUiState by viewModel.accountsUiState.collectAsState()

    // Fetch recommended products when screen loads
    LaunchedEffect(Unit) {
        recommendationViewModel.fetchRecommendedProducts()
    }

    // Debug: Log the recommendations data
    LaunchedEffect(recommendedProducts) {
        println("=== RECOMMENDATION SCREEN DEBUG ===")
        println("Total recommended products fetched: ${recommendedProducts.size}")
        recommendedProducts.forEachIndexed { index, product ->
            println("[$index] Name: ${product.name}")
            println("[$index] Account Type: ${product.accountType}")
            println("[$index] Recommended: ${product.recommended}")
            println("[$index] Is Owned: ${product.isOwned}")
            println("[$index] ID: ${product.id}")
            println("---")
        }
    }

    // Convert RecommendedAccountProducts to AccountProductResponse for UI compatibility
    val recommendations = recommendedProducts.map { recommendedProduct ->
        AccountProductResponse(
            id = recommendedProduct.id,
            name = recommendedProduct.name,
            accountType = recommendedProduct.accountType,
            description = recommendedProduct.description,
            interestRate = recommendedProduct.interestRate.toDouble(),
            minBalanceRequired = recommendedProduct.minBalanceRequired.toDouble(),
            creditLimit = recommendedProduct.creditLimit.toDouble(),
            annualFee = recommendedProduct.annualFee.toDouble(),
            minSalary = recommendedProduct.minSalary.toDouble(),
            image = recommendedProduct.image,
            perks = recommendedProduct.perks,
            categoryIds = recommendedProduct.categoryIds.toList(),
            categoryNames = recommendedProduct.categoryNames.toList(),
            recommended = recommendedProduct.recommended
        )
    }

    val userAccounts = when (accountsUiState) {
        is com.coded.capstone.viewModels.AccountsUiState.Success -> (accountsUiState as com.coded.capstone.viewModels.AccountsUiState.Success).accounts
        else -> emptyList()
    }

    // Success message state
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Handle account creation success
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            // Show success message and reset navigation flag
            successMessage = "Account created successfully!"
            showSuccessMessage = true
            accountViewModel.resetNavigationFlag()
        }
    }

    // Handle account creation state
    LaunchedEffect(accountCreateState) {
        when (accountCreateState) {
            is AccountCreateUiState.Error -> {
                Toast.makeText(
                    context,
                    (accountCreateState as AccountCreateUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    // Function to handle Apply button click
    fun handleApplyClick(accountProduct: AccountProductResponse) {
        accountProduct.id?.let { productId ->
            accountViewModel.createAccount(productId)
        }
    }

    // Function to determine recommendation type based on product
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

    // Function to check if product is owned using isOwned flag from recommended products
    fun isProductOwned(productId: Long?): Boolean {
        return recommendedProducts.find { it.id == productId }?.isOwned ?: false
    }

    // Define Roboto font family
    val robotoFontFamily = FontFamily(Font(R.font.roboto_variablefont_wdthwght))

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFE5E7EB), // Light silver
                            Color(0xFFD1D5DB)  // Silver
                        )
                    )
                )
        ) {
            val currentUiState = recommendedProductsUiState
            when (currentUiState) {
                is RecommendedProductsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF8EC5FF),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading Products...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151),
                                fontFamily = robotoFontFamily
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Finding products tailored for you",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                fontFamily = robotoFontFamily,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                is RecommendedProductsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Failed to Load Recommendations",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                fontFamily = robotoFontFamily,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentUiState.message,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                fontFamily = robotoFontFamily,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { recommendationViewModel.fetchRecommendedProducts() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8EC5FF)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Retry",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                is RecommendedProductsUiState.Success, RecommendedProductsUiState.Idle -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 15.dp, end = 15.dp, bottom = 0.dp)
                    ) {
                        // CLEANER HEADER SECTION - Smaller and more refined
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.1f),
                                                Color.White.copy(alpha = 0.05f)
                                            )
                                        )
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tailored for You",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    fontFamily = robotoFontFamily
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        println("=== MANUAL REFRESH TRIGGERED ===")
                                        recommendationViewModel.fetchRecommendedProducts()
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.1f),
                                                    Color.White.copy(alpha = 0.05f)
                                                )
                                            )
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = onNotificationClick,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.1f),
                                                    Color.White.copy(alpha = 0.05f)
                                                )
                                            )
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // REDESIGNED TITLE SECTION - Smaller, cleaner, no star icon
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Removed star icon completely
                            Text(
                                text = "Products Available",
                                fontSize = 24.sp, // Reduced from 32sp
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                fontFamily = robotoFontFamily,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Discover financial products that match your goals",
                                fontSize = 14.sp, // Reduced from 15sp
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontFamily = robotoFontFamily,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp // Reduced from 20sp
                            )
                        }

                        // RECOMMENDATIONS SECTION WITH GOLD OUTLINE & RECOMMENDED BADGES
                        val uniqueRecommendations = recommendations
                            .filter { product ->
                                // Filter out cashback, business, and salary account cards
                                val productName = product.name?.lowercase() ?: ""
                                val accountType = product.accountType?.lowercase() ?: ""

                                !productName.contains("cashback") &&
                                        !accountType.contains("business") &&
                                        !productName.contains("business") &&
                                        !productName.contains("salary")
                            }
                            .distinctBy { it.name to it.accountType }
                            .sortedWith(compareByDescending<AccountProductResponse> { product ->
                                // First sort criterion: recommended items first (descending: true comes before false)
                                product.recommended
                            }.thenBy { product ->
                                // Second sort criterion: unowned first (false), then owned (true)
                                isProductOwned(product.id)
                            })

                        if (uniqueRecommendations.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(0.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uniqueRecommendations) { product ->
                                    Box(
                                        modifier = Modifier
                                            .width(380.dp)
                                            .height(520.dp)
                                            .background(Color.Transparent, RoundedCornerShape(45.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // WALLET CARD WITH GOLD OUTLINE FOR RECOMMENDED CARDS
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val isRecommended = product.recommended

                                                // Gold border wrapper for recommended cards only
                                                if (isRecommended) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(324.dp) // Slightly larger than card
                                                            .height(204.dp)
                                                            .border(
                                                                width = 4.dp,
                                                                brush = Brush.linearGradient(
                                                                    colors = listOf(
                                                                        Color(0xFFFFD700), // Gold
                                                                        Color(0xFFFFA500), // Orange gold
                                                                        Color(0xFFFFD700)  // Gold
                                                                    )
                                                                ),
                                                                shape = RoundedCornerShape(20.dp)
                                                            )
                                                            .graphicsLayer {
                                                                rotationZ = 90f
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        // RECOMMENDED BADGE - Top right of the gold outline
                                                        Box(
                                                            modifier = Modifier
                                                                .align(Alignment.TopEnd)
                                                                .offset(x = (-8).dp, y = 8.dp)
                                                                .zIndex(10f)
                                                                .graphicsLayer {
                                                                    rotationZ = -90f // Counter-rotate the badge
                                                                }
                                                        ) {
                                                            Card(
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = Color(0xFFFFD700)
                                                                ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                elevation = CardDefaults.cardElevation(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "RECOMMENDED",
                                                                    color = Color.Black,
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                                    fontFamily = robotoFontFamily
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                // WALLET CARD - Rotated 90 degrees
                                                WalletCard(
                                                    account = com.coded.capstone.data.responses.account.AccountResponse(
                                                        id = product.id ?: 0L,
                                                        accountNumber = null,
                                                        balance = java.math.BigDecimal.ZERO,
                                                        ownerId = 0L,
                                                        ownerType = null,
                                                        accountProductId = product.id,
                                                        accountType = product.accountType
                                                    ),
                                                    onCardClick = { onItemClick(product) },
                                                    recommendationType = getRecommendationType(product),
                                                    showDetails = false,
                                                    modifier = Modifier
                                                        .width(320.dp)
                                                        .height(200.dp)
                                                        .graphicsLayer {
                                                            rotationZ = 90f
                                                            scaleX = 1f
                                                            scaleY = 1f
                                                        }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // CLEAN PRODUCT DETAILS - Card style layout
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                product.interestRate?.let {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Color.White.copy(alpha = 0.8f)
                                                        ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        elevation = CardDefaults.cardElevation(2.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                "Interest Rate",
                                                                color = Color(0xFF8EC5FF),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                            Text(
                                                                "${it}%",
                                                                color = Color(0xFF1E1B4B),
                                                                fontWeight = FontWeight.Medium,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                        }
                                                    }
                                                }

                                                product.creditLimit?.let {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Color.White.copy(alpha = 0.8f)
                                                        ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        elevation = CardDefaults.cardElevation(2.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                "Credit Limit",
                                                                color = Color(0xFF8EC5FF),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                            Text(
                                                                "KD ${it.toInt()}",
                                                                color = Color(0xFF1E1B4B),
                                                                fontWeight = FontWeight.Medium,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                        }
                                                    }
                                                }

                                                product.annualFee?.let {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Color.White.copy(alpha = 0.8f)
                                                        ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        elevation = CardDefaults.cardElevation(2.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                "Annual Fee",
                                                                color = Color(0xFF8EC5FF),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                            Text(
                                                                if (it == 0.0) "Free" else "KD ${it.toInt()}",
                                                                color = Color(0xFF1E1B4B),
                                                                fontWeight = FontWeight.Medium,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                        }
                                                    }
                                                }

                                                product.minBalanceRequired?.let {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Color.White.copy(alpha = 0.8f)
                                                        ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        elevation = CardDefaults.cardElevation(2.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                "Min Balance",
                                                                color = Color(0xFF8EC5FF),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                            Text(
                                                                "KD ${it.toInt()}",
                                                                color = Color(0xFF1E1B4B),
                                                                fontWeight = FontWeight.Medium,
                                                                fontSize = 14.sp,
                                                                fontFamily = robotoFontFamily
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(20.dp))

                                            // APPLY NOW BUTTON - Premium styling
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val isOwned = isProductOwned(product.id)

                                                Button(
                                                    onClick = { handleApplyClick(product) },
                                                    enabled = accountCreateState is AccountCreateUiState.Loading == false && !isOwned,
                                                    modifier = Modifier
                                                        .width(330.dp)
                                                        .height(48.dp)
                                                        .then(
                                                            if (!isOwned && accountCreateState !is AccountCreateUiState.Loading)
                                                                Modifier.shadow(
                                                                    elevation = 3.dp,
                                                                    shape = RoundedCornerShape(16.dp),
                                                                    ambientColor = Color.White.copy(alpha = 0.2f)
                                                                )
                                                            else Modifier
                                                        ),
                                                    colors = if (isOwned) {
                                                        ButtonDefaults.buttonColors(
                                                            containerColor = Color.LightGray,
                                                            contentColor = Color.White,
                                                            disabledContainerColor = Color.LightGray,
                                                            disabledContentColor = Color.White
                                                        )
                                                    } else {
                                                        ButtonDefaults.buttonColors(
                                                            containerColor = Color(0xFF8EC5FF),
                                                            contentColor = Color.White,
                                                            disabledContainerColor = Color(0xFF8EC5FF).copy(
                                                                alpha = 0.6f
                                                            ),
                                                            disabledContentColor = Color.White.copy(alpha = 0.6f)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(16.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        if (accountCreateState is AccountCreateUiState.Loading) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(20.dp),
                                                                color = Color.White,
                                                                strokeWidth = 2.dp
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = "Creating Account...",
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                fontFamily = robotoFontFamily,
                                                                color = Color.White
                                                            )
                                                        } else {
                                                            Text(
                                                                text = if (isOwned) "Already Owned" else "Apply Now",
                                                                fontSize = 18.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                fontFamily = robotoFontFamily,
                                                                color = Color.White
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            if (!isOwned) {
                                                                Icon(
                                                                    imageVector = Icons.Filled.ArrowForward,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(20.dp),
                                                                    tint = Color.White
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // EMPTY STATE - Clean design
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(60.dp))
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF8EC5FF).copy(alpha = 0.2f),
                                                        Color(0xFF8EC5FF).copy(alpha = 0.1f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = Color(0xFF8EC5FF),
                                            modifier = Modifier.size(60.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "No Recommendations Yet",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        fontFamily = robotoFontFamily
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Complete your profile to get personalized recommendations",
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp,
                                        fontFamily = robotoFontFamily
                                    )
                                }
                            }
                        }
                    } // Close Column
                } // Close when statement Success/Idle case
            } // Close when statement

            // DECORATIVE ELEMENTS - Subtle background decoration
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(200.dp)
                    .offset(x = 100.dp, y = (-100).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8EC5FF).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 200f
                        ),
                        shape = RoundedCornerShape(100.dp)
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(150.dp)
                    .offset(x = (-75).dp, y = 75.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8EC5FF).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 150f
                        ),
                        shape = RoundedCornerShape(75.dp)
                    )
            )
        }

        // SUCCESS MESSAGE - Account creation success overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f) // Ensure it appears above other elements
        ) {
            AccountCreationSuccessMessage(
                isVisible = showSuccessMessage,
                onDismiss = {
                    showSuccessMessage = false
                },
                fontFamily = robotoFontFamily,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun AccountCreationSuccessMessage(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF8EC5FF),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Account Created Successfully!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your new account has been created and is ready to use.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8EC5FF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = fontFamily
                    )
                }
            }
        }
    }
}