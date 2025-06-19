@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.recommendation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.composables.recommendation.RecommendationCard

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

    val localShopOffers = remember {
        listOf(
            RecommendationItem(
                id = "local_shop_1",
                title = "Al-Mubarak Electronics",
                description = "10% off on all electronics. Exclusive for NBK customers!",
                icon = Icons.Default.Store,
                badgeText = "10% Off",
                type = RecommendationType.CASHBACK_OFFER,
                rating = 4.3f,
                recommendPercent = 87,
                price = "KD 50",
                priceSubtext = "min. spend",
                colors = listOf(Color(0xFF34D399), Color(0xFF059669)),
                detailStats = listOf(
                    StatItem(Icons.Default.LocalOffer, "10%", "discount"),
                    StatItem(Icons.Default.ShoppingCart, "All", "products")
                )
            ),
            RecommendationItem(
                id = "local_shop_2",
                title = "Kuwait Bookstore",
                description = "Buy 2 get 1 free on all books. Show your NBK card at checkout.",
                icon = Icons.Default.MenuBook,
                badgeText = "Buy 2 Get 1",
                type = RecommendationType.GENERAL,
                rating = 4.7f,
                recommendPercent = 92,
                price = "Varies",
                priceSubtext = "per book",
                colors = listOf(Color(0xFF60A5FA), Color(0xFF2563EB)),
                detailStats = listOf(
                    StatItem(Icons.Default.Book, "3 for 2", "deal"),
                    StatItem(Icons.Default.Star, "Top", "titles")
                )
            ),
            RecommendationItem(
                id = "local_shop_3",
                title = "Fresh Bites Cafe",
                description = "Free dessert with every meal for NBK cardholders.",
                icon = Icons.Default.Restaurant,
                badgeText = "Free Dessert",
                type = RecommendationType.CASHBACK_OFFER,
                rating = 4.5f,
                recommendPercent = 90,
                price = "KD 8",
                priceSubtext = "avg. meal",
                colors = listOf(Color(0xFFF59E42), Color(0xFFEA580C)),
                detailStats = listOf(
                    StatItem(Icons.Default.Cake, "Free", "dessert"),
                    StatItem(Icons.Default.Fastfood, "With", "meal")
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Draw vertical black block on the right covering the bottom half
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(60.dp)
                .height(780.dp)
                .background(Color.Black)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // White section with horizontal recommendations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Based on your Accounts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recommendations) { recommendation ->
                        Box(
                            modifier = Modifier.width(300.dp)
                        ) {
                            RecommendationCard(
                                item = recommendation,
                                isExpanded = expandedItemId == recommendation.id,
                                onClick = {
                                    expandedItemId =
                                        if (expandedItemId == recommendation.id) null else recommendation.id
                                    onItemClick(recommendation)
                                },
                                onBookClick = { onActivateClick(recommendation) }
                            )
                        }
                    }
                }
            }

            // Black section with vertical offers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 70.dp, topEnd = 60.dp))
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 16.dp)
                ) {
                    // Page title
                    Text(
                        text = "Tailored to you",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Exclusive deals near you",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Offers list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(localShopOffers) { offer ->
                            RecommendationCard(
                                item = offer,
                                isExpanded = expandedItemId == offer.id,
                                onClick = {
                                    expandedItemId = if (expandedItemId == offer.id) null else offer.id
                                    onItemClick(offer)
                                },
                                onBookClick = { onActivateClick(offer) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    item: RecommendationItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate the card height with smoother animation
    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) 400.dp else 140.dp,
        animationSpec = tween(durationMillis = 500, easing = androidx.compose.animation.core.EaseInOutCubic),
        label = "cardHeight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Left border
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(Color.White)
                .align(Alignment.CenterStart)
        )

        // Card content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header row with icon, title, category, and expand/collapse icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    brush = Brush.linearGradient(item.colors),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = item.title,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (item.badgeText != null) {
                                Text(
                                    text = item.badgeText,
                                    color = item.colors.first(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = item.price,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.priceSubtext,
                                color = Color(0xFFBDBDBD),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = Color(0xFFBDBDBD),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description - shows full text when expanded, truncated when collapsed
                Text(
                    text = item.description,
                    color = Color(0xFFE0E0E0),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Validity/Rating information
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.expiryDate != null) {
                        Text(
                            text = item.expiryDate,
                            color = Color(0xFFBDBDBD),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        text = "★ ${item.rating} (${item.recommendPercent}% recommend)",
                        color = Color(0xFFBDBDBD),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Additional details shown when expanded
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Detail stats section
                    if (item.detailStats.isNotEmpty()) {
                        Text(
                            text = "Key Features:",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(item.detailStats) { stat ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = stat.icon,
                                        contentDescription = null,
                                        tint = item.colors.first(),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stat.value,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = stat.unit,
                                        fontSize = 10.sp,
                                        color = Color(0xFFBDBDBD)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Benefits section
                    Text(
                        text = "Benefits & Terms:",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Premium service with dedicated support\n• Exclusive member benefits and rewards\n• Easy application process\n• Competitive rates and terms\n• 24/7 customer service available\n• Secure and reliable platform",
                        color = Color(0xFFE0E0E0),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action button
                    Button(
                        onClick = onBookClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = item.colors.first()
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.actionText,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tap to collapse indicator
                    Text(
                        text = "Tap to collapse",
                        color = Color(0xFF888888),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}