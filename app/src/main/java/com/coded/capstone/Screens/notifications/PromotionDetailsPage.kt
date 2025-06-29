package com.coded.capstone.Screens.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.coded.capstone.data.responses.promotion.PromotionResponse
import com.coded.capstone.data.responses.promotion.RewardType
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit




// Sample Promotion Data
val samplePromotion = PromotionResponse(
    id = 1L,
    name = "Summer Electronics Mega Sale",
    businessPartnerId = 101L,
    type = RewardType.DISCOUNT,
    _startDate = "2025-06-01",
    _endDate = "2025-08-31",
    description = "Get up to 50% off on all premium electronics including smartphones, laptops, gaming consoles and smart home devices. Limited time offer with exclusive deals on top brands.",
    storeId = 1001L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDetailPage(
    navController: NavController,
    promotionId: String? = null
) {
    // Get promotion from repository based on ID, fallback to sample
    val promotion = remember(promotionId) {
        promotionId?.toLongOrNull()?.let { id ->
            com.coded.capstone.respositories.PromotionRepository.promotions.find { it.id == id }
        } ?: samplePromotion
    }
    val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), promotion.endDate)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8F9FA),
                            Color(0xFFE9ECEF)
                        )
                    )
                )
        ) {
            // You can replace this with actual image
            // Image(
            //     painter = painterResource(id = R.drawable.promotion_background),
            //     contentDescription = "Promotion Background",
            //     modifier = Modifier.fillMaxSize(),
            //     contentScale = ContentScale.Crop
            // )

            // Placeholder background pattern/icon
            Icon(
                imageVector = when (promotion.type) {
                    RewardType.DISCOUNT -> Icons.Default.LocalOffer
                    RewardType.CASHBACK -> Icons.Default.AccountBalanceWallet
                },
                contentDescription = "Background Icon",
                tint = Color.Gray.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
        }

        // Top Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8EC5FF).copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF8EC5FF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.zIndex(1f)
        )

        // Bottom Sheet Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Bottom Sheet Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.White.copy(alpha = 0.5f),
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main Bottom Sheet Content
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)), // Dark navy to match app
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Promotion Type Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when (promotion.type) {
                            RewardType.CASHBACK -> Color(0xFF8EC5FF).copy(alpha = 0.2f)
                            RewardType.DISCOUNT -> Color(0xFFFF6B6B).copy(alpha = 0.2f)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Type: ",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = promotion.type.name,
                                color = when (promotion.type) {
                                    RewardType.CASHBACK -> Color(0xFF8EC5FF)
                                    RewardType.DISCOUNT -> Color(0xFFFF6B6B)
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Promotion Name
                    Text(
                        text = promotion.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = promotion.description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItemCard(
                            icon = Icons.Default.AccessTime,
                            value = "${daysLeft}",
                            label = "Days Left"
                        )
                        StatItemCard(
                            icon = Icons.Default.Percent,
                            value = "50",
                            label = "Max Discount"
                        )
                        StatItemCard(
                            icon = Icons.Default.Store,
                            value = "100+",
                            label = "Stores"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Date Range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Valid From",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = promotion.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Valid Until",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = promotion.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Button
                    Button(
                        onClick = { /* Claim promotion */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8EC5FF)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Claim Promotion",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Claim",
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

@Composable
fun StatItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF8EC5FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

