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
import com.coded.capstone.ui.AppBackground

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

    AppBackground {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Personalize Your Banking",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Never Be KLUEless Again",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Main Content Container - Takes almost full screen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(
                        topStart = 50.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Header
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(bottom = 8.dp)
                                )

                                Text(
                                    text = "Select your favorite categories",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF374151),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Select at least one - up to 3 categories",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                                )

                                Text(
                                    text = "${selectedCategories.size}/3 selected",
                                    fontSize = 10.sp,
                                    color = Color(0xFF374151),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Categories Grid
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
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

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progress indicator
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(
                                                if (index == 0) Color.White else Color.White.copy(alpha = 0.3f),
                                                CircleShape
                                            )
                                    )
                                    if (index < 2) {
                                        Spacer(modifier = Modifier.width(5.dp))
                                    }
                                }
                            }

                            // Error message display
                            if (favCategoryUiState is FavCategoryUiState.Error) {
                                Text(
                                    text = (favCategoryUiState as FavCategoryUiState.Error).message,
                                    color = Color.Red,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
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
                                        .height(40.dp)
                                        .widthIn(min = 80.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF8EC5FF),
                                        disabledContainerColor = Color.White.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (favCategoryUiState is FavCategoryUiState.Loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "NEXT",
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
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
    }
}