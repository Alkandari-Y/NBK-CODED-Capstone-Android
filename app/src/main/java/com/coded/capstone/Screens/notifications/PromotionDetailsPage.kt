package com.coded.capstone.Screens.notifications

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coded.capstone.data.responses.promotion.RewardType
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.RecommendationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.ui.layout.ContentScale
import com.coded.capstone.composables.businessPartners.BusinessLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDetailPage(
    navController: NavController,
    promotionId: String? = null
) {
    val context = LocalContext.current
    val recommendationViewModel: RecommendationViewModel = viewModel { RecommendationViewModel(context) }
    val homeViewModel: HomeScreenViewModel = viewModel { HomeScreenViewModel(context) }

    val promotion by recommendationViewModel.selectedPromotion.collectAsState()
    val partners by recommendationViewModel.partners.collectAsState()
    val favoriteBusinesses by recommendationViewModel.favoriteBusinesses.collectAsState()
    val storeLocations by recommendationViewModel.storeLocations.collectAsState()
    val userXp by homeViewModel.userXp.collectAsState()

    LaunchedEffect(promotionId) {
        promotionId?.let {
            recommendationViewModel.fetchPromotionDetails(it)
            recommendationViewModel.fetchBusinessPartners()
            recommendationViewModel.fetchFavoriteBusinesses()
            recommendationViewModel.fetchStoreLocations()
            homeViewModel.getUserXpInfo()
        }
    }

    //  loading state
    if (promotion == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF8EC5FF),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading promotion details...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    val currentPromotion = promotion!!
    val businessPartner = partners.find { it.id == currentPromotion.businessPartnerId }
    val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), currentPromotion.endDate)
    val isFavorite = favoriteBusinesses.contains(currentPromotion.businessPartnerId)
    val storeLocation = storeLocations.find { it.partnerId == currentPromotion.businessPartnerId }

    val currentTier = userXp?.xpTier?.name ?: "No Tier"
    val tierMultiplier = userXp?.xpTier?.xpPerkMultiplier ?: 1.0
    val xpPerPromotion = userXp?.xpTier?.xpPerPromotion ?: 100
    val totalXp = (xpPerPromotion * tierMultiplier).toInt()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background - White only
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Background icon
            Icon(
                imageVector = when (currentPromotion.type) {
                    RewardType.DISCOUNT -> Icons.Default.LocalOffer
                    RewardType.CASHBACK -> Icons.Default.AccountBalanceWallet
                },
                contentDescription = "Background Icon",
                tint = Color.Gray.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )


            // business logo for promo image - TOP HALF SCREEN
            businessPartner?.let { partner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f) // Take exactly half the screen height
                        .align(Alignment.TopCenter), // Align to top half
                    contentAlignment = Alignment.Center
                ) {
                    BusinessLogo(
                        businessName = partner.name,
                        size = 300.dp, // Adjusted size for half screen
                        shape = null, // No shape - raw image
                        contentScale = ContentScale.Crop // Crop to fill
                    )

                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite Business",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 15.dp) // Just 8dp from edges
                        )
                    }
                }
            }
        }

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when (currentPromotion.type) {
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
                                text = currentPromotion.type.name,
                                color = when (currentPromotion.type) {
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

                    businessPartner?.let { partner ->
                        Text(
                            text = partner.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        // category Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF8EC5FF).copy(alpha = 0.2f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = partner.category.name,
                                fontSize = 12.sp,
                                color = Color(0xFF8EC5FF),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // promo Name
                    Text(
                        text = currentPromotion.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentPromotion.description,
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
                            label = "Days Left",
                            isUrgent = daysLeft <= 3
                        )
                        StatItemCard(
                            icon = Icons.Default.Percent,
                            value = userXp?.xpTier?.perkAmountPercentage?.toString() ?: "N/A",
                            label = when (currentPromotion.type) {
                                RewardType.DISCOUNT -> "% Discount"
                                RewardType.CASHBACK -> "% Cashback"
                            }
                        )
                        StatItemCard(
                            icon = Icons.Default.EmojiEvents,
                            value = "${totalXp} XP",
                            label = "${currentTier} (${tierMultiplier}x)"
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
                                text = currentPromotion.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
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
                                text = currentPromotion.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                color = if (daysLeft <= 3) Color.Red else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Store Location
                    storeLocation?.let { location ->
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Click for location",
                            color = Color(0xFF8EC5FF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location.googleMapUrl))
                                context.startActivity(intent)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StatItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    isUrgent: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUrgent)
                Color.Red.copy(alpha = 0.15f)
            else
                Color.White.copy(alpha = 0.1f)
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
                tint = if (isUrgent) Color.Red else Color(0xFF8EC5FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = if (isUrgent) Color.Red else Color.White,
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