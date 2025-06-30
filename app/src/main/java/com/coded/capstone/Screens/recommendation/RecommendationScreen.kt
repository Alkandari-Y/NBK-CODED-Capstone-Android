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
    
    // Star button and modal state
    var showCategoriesModal by remember { mutableStateOf(false) }
    var selectedProductForModal by remember { mutableStateOf<AccountProductResponse?>(null) }
    


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

    // Function to get categories based on product
    fun getCategories(product: AccountProductResponse?): List<String> {
        return when {
            product?.name?.lowercase()?.contains("travel") == true -> listOf("Travel", "Hotels", "Flights", "Car Rental")
            product?.name?.lowercase()?.contains("family") == true -> listOf("Family", "Education", "Healthcare", "Shopping")
            product?.name?.lowercase()?.contains("entertainment") == true -> listOf("Movies", "Streaming", "Gaming", "Events")
            product?.name?.lowercase()?.contains("shopping") == true -> listOf("Online Shopping", "Retail", "Fashion", "Electronics")
            product?.name?.lowercase()?.contains("dining") == true -> listOf("Restaurants", "Food Delivery", "Cafes", "Bars")
            product?.name?.lowercase()?.contains("health") == true -> listOf("Healthcare", "Pharmacy", "Fitness", "Wellness")
            product?.name?.lowercase()?.contains("education") == true -> listOf("Education", "Books", "Courses", "Software")
            product?.accountType?.lowercase() == "credit" -> listOf("Shopping", "Travel", "Dining", "Entertainment")
            product?.accountType?.lowercase() == "savings" -> listOf("Family", "Education", "Healthcare", "Future Planning")
            product?.accountType?.lowercase() == "debit" -> listOf("Daily Expenses", "Shopping", "Dining", "Transport")
            else -> listOf("General", "Shopping", "Dining", "Entertainment")
        }
    }
    
    // Function to check if product is owned using isOwned flag from recommended products
    fun isProductOwned(productId: Long?): Boolean {
        return recommendedProducts.find { it.id == productId }?.isOwned ?: false
    }

    // Define Roboto font family
    val robotoFontFamily =
        FontFamily.Default // Replace with FontFamily(Font(R.font.roboto_regular)) if you have the font resource

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
                // Header Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
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

                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
    //                    Text(
    //                        text = "Recommendations",
    //                        fontSize = 20.sp,
    //                        fontWeight = FontWeight.Bold,
    //                        color = Color(0xFF1E1B4B),
    //                        textAlign = TextAlign.Center
    //                    )
                        Text(
                            text = "Tailored for You",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
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
                                tint = Color.White,
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
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Subtitle Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Products Available",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            fontFamily = robotoFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Based on Your Accounts",
                            fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                            fontFamily = robotoFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Discover financial products that match your needs and goals",
                            fontSize = 15.sp,
                        color = Color.Gray.copy(alpha = 0.7f),
                            fontFamily = robotoFontFamily,
                        textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                    )
                }

                // Recommendations Section
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
                                    // Centered, rotated WalletCard
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
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
                                    
                                    // Perks/info as premium InfoCards
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            product.interestRate?.let {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Text(
                                                "Interest Rate",
                                                color = Color(0xFF8EC5FF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                            Text(
                                                "${it}%",
                                                color = Color(0xFF1E1B4B),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                        }
                                }
                                product.creditLimit?.let {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Text(
                                                "Credit Limit",
                                                color = Color(0xFF8EC5FF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                            Text(
                                                "KD ${it.toInt()}",
                                                color = Color(0xFF1E1B4B),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                        }
                                }
                                product.annualFee?.let {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Text(
                                                "Annual Fee",
                                                color = Color(0xFF8EC5FF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                            Text(
                                                if (it == 0.0) "Free" else "KD ${it.toInt()}",
                                                color = Color(0xFF1E1B4B),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                        }
                                    }
                                    product.minBalanceRequired?.let {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Text(
                                                "Min Balance",
                                                color = Color(0xFF8EC5FF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                            Text(
                                                "KD ${it.toInt()}",
                                                color = Color(0xFF1E1B4B),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                fontFamily = robotoFontFamily
                                            )
                                        }
                                    }
                                    product.minSalary?.let {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceAround
                                            ) {
                                                Text(
                                                    "Min Salary",
                                                    color = Color(0xFF8EC5FF),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    fontFamily = robotoFontFamily
                                                )
                                                Text(
                                                    "KD ${it.toInt()}",
                                                    color = Color(0xFF1E1B4B),
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 15.sp,
                                                    fontFamily = robotoFontFamily
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                // Premium Apply Button
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
                                                color = Color(0xFF1E1B4B),
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
                            
                            // Star Button - Top Right of each card
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .zIndex(10f)
                            ) {
                                IconButton(
                                    onClick = { 
                                        selectedProductForModal = product
                                        showCategoriesModal = true 
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF8EC5FF).copy(alpha = 0.9f),
                                                    Color(0xFF8EC5FF).copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                ) {
                                    SharpStarsIcon(
                                        modifier = Modifier.size(30.dp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty State
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
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Complete your profile to get personalized recommendations",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
            } // Close Column
                } // Close when statement Success/Idle case
            } // Close when statement

        // Decorative Elements
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



//                         shape = RoundedCornerShape(75.dp)
//                     )
//             )
        }

        // Success Message - positioned in the center of the screen
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

        // Categories Modal
        if (showCategoriesModal && selectedProductForModal != null) {
            Dialog(
                onDismissRequest = { 
                    showCategoriesModal = false
                    selectedProductForModal = null
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF23272E)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Card Categories",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = { 
                                    showCategoriesModal = false
                                    selectedProductForModal = null
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Categories Grid
                        val categories = getCategories(selectedProductForModal)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF8EC5FF).copy(alpha = 0.15f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = category,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF8EC5FF)
                                        )
//                                        Icon(
//                                            imageVector = Icons.Default.ArrowForward,
//                                            contentDescription = null,
//                                            tint = Color(0xFF8EC5FF).copy(alpha = 0.7f),
//                                            modifier = Modifier.size(16.dp)
//                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Description
                        Text(
                            text = "This card offers rewards and benefits in these categories",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
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
