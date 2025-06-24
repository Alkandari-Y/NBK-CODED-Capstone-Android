package com.coded.capstone.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coded.capstone.composables.onBoarding.CategoryCard
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.respositories.CategoryRepository
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.viewModels.FavCategoryUiState

data class SpendingCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val description: String,
    val topReward: String,
    val bestCard: String,
    val color: Color
)

@Composable
fun CategoryOnBoarding(navController: NavController, viewModel: HomeScreenViewModel) {
    var selectedCategories by remember { mutableStateOf(listOf<String>()) }
    val categories by viewModel.categories.collectAsState()
    val favCategoryUiState by viewModel.favCategoryUiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    // Handle navigation on success
    LaunchedEffect(favCategoryUiState) {
        when (favCategoryUiState) {
            is FavCategoryUiState.Success -> {
                navController.navigate(NavRoutes.NAV_ROUTE_VENDORS_ONBOARDING)
            }
            else -> {}
        }
    }

    fun toggleCategory(categoryId: String) {
        selectedCategories = if (selectedCategories.contains(categoryId)) {
            selectedCategories.filter { it != categoryId }
        } else if (selectedCategories.size < 3) {
            selectedCategories + categoryId
        } else {
            selectedCategories
        }
    }

    fun submitFavoriteCategories() {
        if (selectedCategories.isEmpty()) return
        viewModel.submitFavoriteCategories(selectedCategories)
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
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF20436C),
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Select your favorite categories",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Select at least one - up to 3 categories",
                            fontSize = 16.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )

                        Text(
                            text = "${selectedCategories.size}/3 selected",
                            fontSize = 14.sp,
                            color = Color(0xFF212937),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Categories Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = categories,
                            key = { category -> category.id }
                        ) { category ->
                            CategoryCard(
                                category = category,
                                isSelected = selectedCategories.contains(category.id.toString()),
                                isDisabled = !selectedCategories.contains(category.id.toString()) && selectedCategories.size >= 3,
                                onClick = { toggleCategory(category.id.toString()) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Progress indicator
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
                                        if (index == 0) Color(0xFF212937) else Color(0xFFE5E7EB),
                                        CircleShape
                                    )
                            )
                            if (index < 2) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Error message display
                    if (favCategoryUiState is FavCategoryUiState.Error) {
                        Text(
                            text = (favCategoryUiState as FavCategoryUiState.Error).message,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Next Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { submitFavoriteCategories() },
                            enabled = selectedCategories.isNotEmpty() && favCategoryUiState !is FavCategoryUiState.Loading,
                            modifier = Modifier
                                .height(56.dp)
                                .widthIn(min = 120.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF212937),
                                disabledContainerColor = Color(0xFFD1D5DB)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (favCategoryUiState is FavCategoryUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "NEXT",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
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