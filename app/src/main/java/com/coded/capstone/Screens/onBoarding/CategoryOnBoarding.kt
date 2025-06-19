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
import androidx.navigation.NavController
import com.coded.capstone.composables.onBoarding.CategoryCard
import com.coded.capstone.navigation.NavRoutes

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
fun CategoryOnBoarding(navController: NavController) {
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    val categories = listOf(
        SpendingCategory(
            id = "dining",
            name = "Dining",
            icon = Icons.Default.Restaurant,
            description = "Kuwait's largest dining network",
            topReward = "Up to 10% NBK KWT Points",
            bestCard = "NBK KWT Visa Infinite",
            color = Color(0xFFEF4444)
        ),
        SpendingCategory(
            id = "travel",
            name = "Travel",
            icon = Icons.Default.Flight,
            description = "Miles + lounge access",
            topReward = "5 NBK Miles Points per KD",
            bestCard = "NBK Miles World Mastercard",
            color = Color(0xFF3B82F6)
        ),
        SpendingCategory(
            id = "shopping",
            name = "Shopping",
            icon = Icons.Default.ShoppingBag,
            description = "Local & international brands",
            topReward = "Up to 8% Aura Points",
            bestCard = "NBK-Aura World Mastercard",
            color = Color(0xFFEC4899)
        ),
        SpendingCategory(
            id = "technology",
            name = "Technology",
            icon = Icons.Default.Smartphone,
            description = "Electronics & digital payments",
            topReward = "10% NBK KWT Points at X-cite",
            bestCard = "NBK KWT Visa Infinite",
            color = Color(0xFF8B5CF6)
        ),
        SpendingCategory(
            id = "lifestyle",
            name = "Lifestyle",
            icon = Icons.Default.Home,
            description = "Home, beauty & wellness",
            topReward = "NBK Rewards Points",
            bestCard = "NBK Rewards Program",
            color = Color(0xFF10B981)
        ),
        SpendingCategory(
            id = "entertainment",
            name = "Entertainment",
            icon = Icons.Default.Movie,
            description = "Streaming, cinema & events",
            topReward = "Up to 24% Cashback",
            bestCard = "NBK 247 Cashback",
            color = Color(0xFFF59E0B)
        )
    )

    fun toggleCategory(categoryId: String) {
        if (selectedCategories.contains(categoryId)) {
            selectedCategories = selectedCategories - categoryId
        } else if (selectedCategories.size < 3) {
            selectedCategories = selectedCategories + categoryId
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
                        items(categories) { category ->
                            CategoryCard(
                                category = category,
                                isSelected = selectedCategories.contains(category.id),
                                isDisabled = !selectedCategories.contains(category.id) && selectedCategories.size >= 3,
                                onClick = { toggleCategory(category.id) }
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

                    // Next Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // Navigate to vendors onboarding with selected categories
                                val categoriesString = selectedCategories.joinToString(",")
                                navController.navigate(NavRoutes.NAV_ROUTE_VENDORS_ONBOARDING)
                            },
                            enabled = selectedCategories.isNotEmpty(),
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

