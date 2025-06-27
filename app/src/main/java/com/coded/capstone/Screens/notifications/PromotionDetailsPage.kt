package com.coded.capstone.Screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.data.responses.promotion.PromotionResponse
import com.coded.capstone.data.responses.promotion.RewardType
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
fun PromotionDetailPage(navController: NavController) {
    val promotion = remember { samplePromotion }
    val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), promotion.endDate)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Promotion Details",
                        color = Color(0xFF1E1E1E),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1E1E)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share promotion */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF8EC5FF)
                        )
                    }
                    IconButton(onClick = { /* Favorite promotion */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color(0xFF8EC5FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E1E1E),
                    navigationIconContentColor = Color(0xFF1E1E1E)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Promotion Icon/Image Placeholder
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(
                                        Color(0xFF8EC5FF),
                                        Color(0xFF6A9EFF)
                                    )
                                )
                            )
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (promotion.type) {
                                RewardType.DISCOUNT -> Icons.Default.LocalOffer
                                RewardType.CASHBACK -> Icons.Default.AccountBalanceWallet
                            },
                            contentDescription = "Promotion Icon",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Promotion Type Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF8EC5FF).copy(alpha = 0.1f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = promotion.type.name,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = Color(0xFF8EC5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Promotion Name
                    Text(
                        text = promotion.name,
                        color = Color(0xFF1E1E1E),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = promotion.description,
                        color = Color(0xFF6D6D6D),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            icon = Icons.Default.AccessTime,
                            value = "${daysLeft}",
                            label = "Days Left"
                        )
                        StatItem(
                            icon = Icons.Default.Percent,
                            value = "50",
                            label = "Max Discount"
                        )
                        StatItem(
                            icon = Icons.Default.Store,
                            value = "100+",
                            label = "Stores"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Date Range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Valid From",
                                color = Color(0xFF8E8E93),
                                fontSize = 12.sp
                            )
                            Text(
                                text = promotion.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                color = Color(0xFF1E1E1E),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Valid Until",
                                color = Color(0xFF8E8E93),
                                fontSize = 12.sp
                            )
                            Text(
                                text = promotion.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                color = Color(0xFF1E1E1E),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

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
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF8EC5FF).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF8EC5FF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = Color(0xFF1E1E1E),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFF8E8E93),
            fontSize = 12.sp
        )
    }
}

// Preview
@Composable
fun PromotionDetailPagePreview() {
    MaterialTheme {
        // PromotionDetailPage() // Commented out since it needs NavController now
    }
}