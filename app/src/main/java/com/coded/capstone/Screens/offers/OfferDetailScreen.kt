package com.coded.capstone.screens.offers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.viewModels.PromotionDetailViewModel
import com.coded.capstone.viewModels.PromotionDetailUiState
import com.coded.capstone.viewModels.PromotionDetailData
import com.coded.capstone.navigation.NavRoutes
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDetailScreen(
    offerId: Long,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: PromotionDetailViewModel = remember { PromotionDetailViewModel(context) }
    val uiState by viewModel.uiState
    val isAuthenticated = viewModel.isUserAuthenticated()

    LaunchedEffect(offerId) {
        viewModel.loadOfferDetails(offerId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with back button
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (isAuthenticated) {
                            // Logged-in user: Navigate to home
                            navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                                popUpTo(0) // Clear back stack
                            }
                        } else {
                            // Guest user: Navigate to login with return URL
                            navController.navigate("${NavRoutes.NAV_ROUTE_LOGIN}?returnUrl=offers/$offerId")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Main content
        when (val state = uiState) {
            is PromotionDetailUiState.Loading -> OfferLoadingScreen()
            is PromotionDetailUiState.Success -> OfferDetailContent(
                offerData = state.offerData,
                isAuthenticated = isAuthenticated,
                onLoginClick = {
                    navController.navigate("${NavRoutes.NAV_ROUTE_LOGIN}?returnUrl=offers/$offerId")
                },
                viewModel = viewModel
            )
            is PromotionDetailUiState.Error -> OfferErrorScreen(
                message = state.message,
                onRetry = { viewModel.loadOfferDetails(offerId) }
            )
        }
    }
}

@Composable
fun OfferDetailContent(
    offerData: PromotionDetailData,
    isAuthenticated: Boolean,
    onLoginClick: () -> Unit,
    viewModel: PromotionDetailViewModel
) {
    val context = LocalContext.current

    // Move resource checking outside of Box composable
    val resourceName = viewModel.getBusinessImageResource(offerData.businessPartner.name)
    val businessImageId = remember(resourceName) {
        try {
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        } catch (_: Exception) { 0 }
    }
    val defaultImageId = remember {
        try {
            context.resources.getIdentifier("default_offer", "drawable", context.packageName)
        } catch (_: Exception) { 0 }
    }
    val expiredImageId = remember {
        try {
            context.resources.getIdentifier("expired_offer", "drawable", context.packageName)
        } catch (_: Exception) { 0 }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Half - Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFFF59D)) // Light yellow background like the reference
        ) {
            if (offerData.isExpired) {
                // Show default image for expired offers
                if (defaultImageId != 0) {
                    Image(
                        painter = painterResource(id = expiredImageId),
                        contentDescription = "Expired offer",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback for expired offers if default image doesn't exist
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EXPIRED",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Try to load business-specific image, fallback to default
                when {
                    businessImageId != 0 -> {
                        // Business-specific image exists
                        Image(
                            painter = painterResource(id = businessImageId),
                            contentDescription = offerData.businessPartner.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    defaultImageId != 0 -> {
                        // Use default image
                        Image(
                            painter = painterResource(id = defaultImageId),
                            contentDescription = "Default offer image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    else -> {
                        // No images available, show text placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp)
                                .background(Color.White.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = offerData.businessPartner.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        // Bottom Half - Curved Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(
                topStart = 70.dp,
                topEnd = 0.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            if (offerData.isExpired) {
                ExpiredOfferPanel()
            } else {
                OfferInfoPanel(
                    offerData = offerData,
                    isAuthenticated = isAuthenticated,
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}

@Composable
fun OfferInfoPanel(
    offerData: PromotionDetailData,
    isAuthenticated: Boolean,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business name
        Text(
            text = offerData.businessPartner.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Promotion name
        Text(
            text = offerData.promotion.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Description
        Text(
            text = offerData.promotion.description,
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Expiration date
        Text(
            text = "Expires: ${offerData.promotion.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        // Category
        Text(
            text = "Category: ${offerData.businessPartner.category.name}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        // Authentication-specific content
        if (isAuthenticated) {
            // Show XP information for logged-in users
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "You could earn:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${offerData.promotion.xp} XP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50) // Green color
                    )
                }
            }
        } else {
            // Show login CTA for guests
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4) // Purple color
                )
            ) {
                Text(
                    text = "Log in to see your benefits",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ExpiredOfferPanel() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "This offer has expired",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check back for new offers!",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OfferLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF6750A4)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Fetching your offer...",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun OfferErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading offer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}