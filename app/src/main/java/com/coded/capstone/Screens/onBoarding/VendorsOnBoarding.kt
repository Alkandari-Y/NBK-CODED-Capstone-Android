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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.composables.onBoarding.NBKVendorCard
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.respositories.CategoryRepository
import com.coded.capstone.viewModels.FavBusinessUiState

// Category icon mapping
fun getCategoryIcon(category: String): ImageVector = when (category) {
    "dining" -> Icons.Default.Restaurant
    "travel" -> Icons.Default.Flight
    "shopping" -> Icons.Default.ShoppingBag
    "technology" -> Icons.Default.Smartphone
    "lifestyle" -> Icons.Default.Home
    "entertainment" -> Icons.Default.Movie
    else -> Icons.Default.Store
}

@Composable
fun VendorsOnBoarding(
    navController: NavController,
    viewModel: RecommendationViewModel
) {
    var selectedVendors by remember { mutableStateOf(setOf<Long>()) }
    var searchQuery by remember { mutableStateOf("") }
    val partners by viewModel.partners.collectAsState()
    val favBusinessUiState by viewModel.favBusinessUiState.collectAsState()
    
    // Fetch business partners when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchBusinessPartners()
    }
    
    // Handle navigation after successful submission
    LaunchedEffect(favBusinessUiState) {
        when (favBusinessUiState) {
            is FavBusinessUiState.Success -> {
                navController.navigate(NavRoutes.NAV_ROUTE_CARD_SUGGESTION)
            }
            is FavBusinessUiState.Error -> {
                // Handle error if needed
            }
            else -> {}
        }
    }

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
        } else {
            selectedVendors = selectedVendors + partnerId
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0E0E0),
                        Color(0xFF212937).copy(alpha = 0.05f),
                        Color(0xFF212937).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color(0xFF081538),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "KLUE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Personalize Your Banking",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Text(
                text = "Never Be KLUEless Again",
                fontSize = 16.sp,
                color = Color(0xFF212937),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Store,
                            contentDescription = null,
                            tint = Color(0xFF20436C),
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Select your favorite vendors",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Personalized offers from Kuwait's best merchants",
                            fontSize = 16.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )

                        Text(
                            text = "Based on your interests: ${CategoryRepository.favCategories.map { it.categoryId }.joinToString(", ")}",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "${selectedVendors.size} vendors selected",
                            fontSize = 14.sp,
                            color = Color(0xFF212937),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search vendors, categories...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF6B7280)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF212937),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    // Vendors Scrollable Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredPartners) { partner ->
                            NBKVendorCard(
                                vendor = partner,
                                isSelected = partner.id?.let { selectedVendors.contains(it) } ?: false,
                                onClick = { partner.id?.let { toggleVendor(it) } }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (index == 1) Color(0xFF212937) else Color(0xFFE5E7EB),
                                        CircleShape
                                    )
                            )
                            if (index < 2) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                navController.navigate(NavRoutes.NAV_ROUTE_CARD_SUGGESTION)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF6B7280)
                            )
                        ) {
                            Text("Skip this step")
                        }

                        Button(
                            onClick = {
                                val selectedBusinessIds = selectedVendors.map { it.toString() }
                                viewModel.submitFavoriteBusinesses(selectedBusinessIds)
                            },
                            modifier = Modifier
                                .height(56.dp)
                                .widthIn(min = 120.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF212937)
                            )
                        ) {
                            Text(
                                text = "Continue",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

