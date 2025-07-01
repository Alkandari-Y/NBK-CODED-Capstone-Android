package com.coded.capstone.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.composables.businessPartners.BusinessLogo
import com.coded.capstone.viewModels.FavBusinessUiState
import kotlinx.coroutines.delay

@Composable
fun VendorsOnBoarding(
    navController: NavController,
    viewModel: RecommendationViewModel
) {
    var selectedVendors by remember { mutableStateOf(setOf<Long>()) }
    var searchQuery by remember { mutableStateOf("") }
    val partners by viewModel.partners.collectAsState()
    val favBusinessUiState by viewModel.favBusinessUiState.collectAsState()

    // Animation states - same as CategoryOnBoarding
    var cardVisible by remember { mutableStateOf(false) }
    val cardOffsetY by animateDpAsState(
        targetValue = if (cardVisible) 140.dp else 800.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "card_slide_up"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "card_fade_in"
    )

    // Scroll state for dynamic indicator
    val scrollState = rememberLazyGridState()

    // Trigger animation and fetch data
    LaunchedEffect(Unit) {
        delay(300)
        cardVisible = true
        viewModel.fetchBusinessPartners()
    }

    // Handle navigation on success
    LaunchedEffect(favBusinessUiState) {
        when (favBusinessUiState) {
            is FavBusinessUiState.Success -> {
                navController.navigate(NavRoutes.NAV_ROUTE_CARD_SUGGESTION)
            }
            else -> {}
        }
    }

    // Filter partners based on search
    val filteredPartners = if (searchQuery.isBlank()) {
        partners
    } else {
        partners.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.name.contains(searchQuery, ignoreCase = true)
        }
    }

    fun toggleVendor(partnerId: Long) {
        if (selectedVendors.contains(partnerId)) {
            selectedVendors = selectedVendors - partnerId
        } else if (selectedVendors.size < 3) {
            selectedVendors = selectedVendors + partnerId
        }
    }

    fun submitFavoriteVendors() {
        val selectedBusinessIds = selectedVendors.map { it.toString() }
        viewModel.submitFavoriteBusinesses(selectedBusinessIds)
    }

    fun skipStep() {
        navController.navigate(NavRoutes.NAV_ROUTE_CARD_SUGGESTION)
    }

    // Use Scaffold to ensure bottom bar is always visible
    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    // Skip button - Left
                    TextButton(
                        onClick = { skipStep() },
                        modifier = Modifier.align(Alignment.CenterStart),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF6B7280)
                        )
                    ) {
                        Text(
                            text = "SKIP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Progress indicator - Center
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (index == 1) Color(0xFF374151) else Color(0xFFD1D5DB),
                                        CircleShape
                                    )
                            )
                            if (index < 2) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Continue Button - Right
                    Button(
                        onClick = { submitFavoriteVendors() },
                        enabled = favBusinessUiState !is FavBusinessUiState.Loading,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .height(44.dp)
                            .widthIn(min = 120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8EC5FF),
                            disabledContainerColor = Color(0xFFE5E7EB)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (favBusinessUiState is FavBusinessUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "CONTINUE",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF374151))
                .padding(paddingValues)
        ) {
            // Logo in top section - same as CategoryOnBoarding
            Image(
                painter = painterResource(id = R.drawable.klue),
                contentDescription = "KLUE Logo",
                modifier = Modifier
                    .size(90.dp)
                    .offset(y = 50.dp)
                    .align(Alignment.TopCenter)
            )
            Spacer(modifier = Modifier.width(15.dp))
            // Animated Main Content Card - same as CategoryOnBoarding
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .offset(y = cardOffsetY)
                    .alpha(cardAlpha),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(
                    topStart = 50.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header - same structure as CategoryOnBoarding
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 40.dp)
                        ) {
                            Text(
                                text = "Personalize Your Experience",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Select up to 3 of your favorite shops",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp)
                            )

                            Text(
                                text = "${selectedVendors.size}/3 selected",
                                fontSize = 12.sp,
                                color = Color(0xFF374151),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }

                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search vendors, categories...",
                                    color = Color(0xFF9CA3AF)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8EC5FF),
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedTextColor = Color(0xFF374151)
                            )
                        )

                        // Vendors Grid with dynamic scroll indicator
                        Box(modifier = Modifier.weight(1f)) {
                            LazyVerticalGrid(
                                state = scrollState,
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(bottom = 20.dp)
                            ) {
                                items(
                                    items = filteredPartners,
                                    key = { partner -> partner.id ?: 0 }
                                ) { partner ->
                                    Card(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (partner.id?.let { selectedVendors.contains(it) } == true)
                                            BorderStroke(2.dp, Color(0xFF8EC5FF)) else null,
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 2.dp
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        onClick = {
                                            partner.id?.let { partnerId ->
                                                if (selectedVendors.contains(partnerId) || selectedVendors.size < 3) {
                                                    toggleVendor(partnerId)
                                                }
                                            }
                                        }
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            // Selection indicator with checkmark - same as CategoryOnBoarding
                                            if (partner.id?.let { selectedVendors.contains(it) } == true) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .offset(x = (-6).dp, y = 6.dp)
                                                        .background(Color(0xFF8EC5FF), CircleShape)
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .align(Alignment.Center)
                                                    )
                                                }
                                            }

                                            // ONLY LOGO - NO TEXT - Centered in the grid square
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                BusinessLogo(
                                                    businessName = partner.name,
                                                    size = 160.dp,
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Dynamic scroll indicator - same as CategoryOnBoarding
                            val scrollProgress = remember {
                                derivedStateOf {
                                    if (scrollState.layoutInfo.totalItemsCount == 0) 0f
                                    else {
                                        val firstVisibleItem = scrollState.firstVisibleItemIndex.toFloat()
                                        val totalItems = scrollState.layoutInfo.totalItemsCount.toFloat()
                                        (firstVisibleItem / (totalItems - 4)).coerceIn(0f, 1f)
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp)
                                    .width(6.dp)
                                    .height(200.dp)
                            ) {
                                // Track background
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Color(0xFF9CA3AF).copy(alpha = 0.3f),
                                            RoundedCornerShape(3.dp)
                                        )
                                )

                                // Moving thumb
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(40.dp)
                                        .offset(y = (160.dp * scrollProgress.value))
                                        .background(
                                            Color(0xFF9CA3AF).copy(alpha = 0.8f),
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                            }
                        }
                    }

                    // Error message display
                    if (favBusinessUiState is FavBusinessUiState.Error) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .offset(y = (-80).dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = (favBusinessUiState as FavBusinessUiState.Error).message,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}