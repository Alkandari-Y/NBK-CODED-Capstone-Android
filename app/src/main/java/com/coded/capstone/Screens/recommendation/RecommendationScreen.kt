@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.recommendation

import android.widget.Toast
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
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.AccountCreateUiState
import com.coded.capstone.composables.wallet.WalletCard
import androidx.compose.foundation.layout.FlowRow
import com.coded.capstone.composables.recommendation.InfoCard
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.coded.capstone.R


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
    val accountCreateState by accountViewModel.accountUiState.collectAsState()
    val shouldNavigate by accountViewModel.shouldNavigate.collectAsState()

    val recommendations by viewModel.accountProducts.collectAsState()
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val userAccounts = when (accountsUiState) {
        is com.coded.capstone.viewModels.AccountsUiState.Success -> (accountsUiState as com.coded.capstone.viewModels.AccountsUiState.Success).accounts
        else -> emptyList()
    }

    // Handle account creation success
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            // Show success message and reset navigation flag
            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
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

    // Define Roboto font family
    val robotoFontFamily =
        FontFamily.Default // Replace with FontFamily(Font(R.font.roboto_regular)) if you have the font resource

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, bottom = 0.dp)
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
                        text = "Recommendations",
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
                        // Filter out cashback and business account cards
                        val productName = product.name?.lowercase() ?: ""
                        val accountType = product.accountType?.lowercase() ?: ""
                        
                        !productName.contains("cashback") && 
                        !accountType.contains("business") &&
                        !productName.contains("business") &&
                        !productName.contains("salary")
                    }
                    .distinctBy { it.name to it.accountType }
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
                                                horizontalArrangement = Arrangement.SpaceBetween
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
                                                horizontalArrangement = Arrangement.SpaceBetween
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
                                                horizontalArrangement = Arrangement.SpaceBetween
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
                                                horizontalArrangement = Arrangement.SpaceBetween
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
                                                horizontalArrangement = Arrangement.SpaceBetween
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
                                        val userHasCard = userAccounts.any { acc ->
                                            acc.accountProductId == product.id
                                        }
                                        Button(
                                            onClick = { handleApplyClick(product) },
                                            enabled = accountCreateState is AccountCreateUiState.Loading == false && !userHasCard,
                                            modifier = Modifier
                                                .width(330.dp)
                                                .height(48.dp)
                                                .then(
                                                    if (!userHasCard && accountCreateState !is AccountCreateUiState.Loading)
                                                        Modifier.shadow(
                                                            elevation = 12.dp,
                                                            shape = RoundedCornerShape(16.dp),
                                                            ambientColor = Color.White.copy(alpha = 0.3f)
                                                        )
                                                    else Modifier
                                                ),
                                            colors = if (userHasCard) {
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
                                                        text = if (userHasCard) "Already Owned" else "Apply Now",
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = robotoFontFamily,
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    if (!userHasCard) {
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
            }

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
        }
    }

}