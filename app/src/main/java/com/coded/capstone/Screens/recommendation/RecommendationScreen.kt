@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.recommendation


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RecommendationItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badgeText: String? = null,
    val expiryDate: String? = null,
    val tags: List<String> = emptyList(),
    val actionText: String = "Activate",
    val type: RecommendationType,
    val rating: Float = 4.5f,
    val recommendPercent: Int = 85,
    val price: String = "$900",
    val priceSubtext: String = "/ day",
    val detailStats: List<StatItem> = emptyList(),
    val colors: List<Color> = listOf(Color(0xFFFDD835), Color(0xFFFFB74D))
)

data class StatItem(
    val icon: ImageVector,
    val value: String,
    val unit: String
)

enum class RecommendationType {
    CASHBACK_OFFER,
    INSURANCE,
    MOBILE_TOPUP,
    GENERAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onItemClick: (RecommendationItem) -> Unit = {},
    onActivateClick: (RecommendationItem) -> Unit = {}
) {
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    val recommendations = remember {
        listOf(
            RecommendationItem(
                id = "nbk_platinum_card",
                title = "NBK Platinum Card",
                description = "Premium rewards with 5% cashback on dining, shopping, and fuel. Enjoy exclusive privileges and worldwide acceptance.",
                icon = Icons.Default.CreditCard,
                badgeText = "5% Cashback",
                expiryDate = "Limited Time Offer",
                type = RecommendationType.CASHBACK_OFFER,
                rating = 4.8f,
                recommendPercent = 94,
                price = "KD 250",
                priceSubtext = "annual fee",
                colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                detailStats = listOf(
                    StatItem(Icons.Default.CreditCard, "5%", "cashback"),
                    StatItem(Icons.Default.Flight, "Free", "lounge access"),
                    StatItem(Icons.Default.LocalOffer, "2X", "reward points")
                )
            ),
            RecommendationItem(
                id = "nbk_wealth_management",
                title = "NBK Wealth Management",
                description = "Personalized investment solutions with dedicated relationship manager. Minimum investment KD 50,000.",
                icon = Icons.Default.TrendingUp,
                badgeText = "Premium Service",
                tags = listOf("Investment", "Wealth"),
                type = RecommendationType.INSURANCE,
                rating = 4.9f,
                recommendPercent = 98,
                price = "8.5%",
                priceSubtext = "expected return",
                colors = listOf(Color(0xFF1E3A8A), Color(0xFF3730A3)),
                detailStats = listOf(
                    StatItem(Icons.Default.AccountBalance, "50K", "min. investment"),
                    StatItem(Icons.Default.Person, "Dedicated", "advisor"),
                    StatItem(Icons.Default.Security, "KISR", "compliant")
                )
            ),
            RecommendationItem(
                id = "nbk_digital_banking",
                title = "NBK Digital Plus",
                description = "Enhanced digital banking with instant transfers, bill payments, and 24/7 customer support through mobile app.",
                icon = Icons.Default.PhoneAndroid,
                badgeText = "Free Setup",
                type = RecommendationType.MOBILE_TOPUP,
                rating = 4.6f,
                recommendPercent = 89,
                price = "Free",
                priceSubtext = "monthly",
                colors = listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)),
                detailStats = listOf(
                    StatItem(Icons.Default.Speed, "Instant", "transfers"),
                    StatItem(Icons.Default.Security, "Biometric", "security"),
                    StatItem(Icons.Default.Support, "24/7", "support")
                )
            ),
            RecommendationItem(
                id = "nbk_business_account",
                title = "NBK Business Pro",
                description = "Comprehensive business banking solutions with corporate cards, payroll services, and trade finance facilities.",
                icon = Icons.Default.Business,
                badgeText = "No Monthly Fees",
                type = RecommendationType.GENERAL,
                rating = 4.7f,
                recommendPercent = 91,
                price = "KD 0",
                priceSubtext = "setup fee",
                colors = listOf(Color(0xFF4338CA), Color(0xFF6366F1)),
                detailStats = listOf(
                    StatItem(Icons.Default.CorporateFare, "Multi", "user access"),
                    StatItem(Icons.Default.Receipt, "Automated", "accounting"),
                    StatItem(Icons.Default.Public, "Trade", "finance")
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Based on your Accounts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )


                    LazyRow( 
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recommendations) { recommendation ->
                            Box(
                                modifier = Modifier
                                    .width(300.dp)  // Fixed width for both states
                            ) {
                                RecommendationCard(
                                    item = recommendation,
                                    isExpanded = expandedItemId == recommendation.id,
                                    onClick = {
                                        expandedItemId = if (expandedItemId == recommendation.id) null else recommendation.id
                                        onItemClick(recommendation)
                                    },
                                    onBookClick = { onActivateClick(recommendation) }
                                )
                            }
                        }
                    }
                }
            }

       }


    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecommendationCard(
    item: RecommendationItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onBookClick: () -> Unit
) {
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 400.dp else 200.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "height_animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = item.colors.first()
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Product",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Badge
                Surface(
                    modifier = Modifier.padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = item.badgeText.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Section
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    item.detailStats.forEach { stat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stat.value,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = stat.unit,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Apply Now Button
                Button(
                    onClick = onBookClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = item.colors.first()
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Apply Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Value",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = item.price,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = item.priceSubtext,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expand",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

