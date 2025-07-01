package com.coded.capstone.screens.xp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coded.capstone.data.responses.xp.XpGainMethod
import com.coded.capstone.data.responses.xp.XpHistoryDto
import com.coded.capstone.data.responses.xp.XpTierResponse
import com.coded.capstone.respositories.XpRepository
import com.coded.capstone.viewModels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XpTierScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(context) as T
            }
        }
    )

    val userXp by viewModel.userXp.collectAsState()
    val xpTiers by viewModel.xpTiers.collectAsState()
    val xpHistory by viewModel.userXpHistory.collectAsState()

    // Fetch data when screen loads
    LaunchedEffect(Unit) {
        viewModel.getUserXpInfo()
        viewModel.getUserXpHistory()
        viewModel.fetchXpTiers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header Card - matching profile design
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "XP History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8EC5FF).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF8EC5FF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                // Extra space to extend the dark section
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // XP Display
            Text(
                text = "${userXp?.userXpAmount ?: 0}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8EC5FF)
            )
            Text(
                text = "Total XP",
                fontSize = 16.sp,
                color = Color(0xFF4A4A4A).copy(alpha = 0.7f)
            )

            // Status Message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Current XP Range: ${userXp?.xpTier?.minXp ?: 0} - ${userXp?.xpTier?.maxXp ?: 0}",
                    fontSize = 14.sp,
                    color = Color(0xFF8EC5FF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color(0xFF8EC5FF),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Points Left Progress
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val xpLeft = userXp?.let { info ->
                        info.xpTier?.let { tier ->
                            tier.maxXp.toLong() - info.userXpAmount
                        }
                    } ?: 0L
                    
                    Text(
                        text = "$xpLeft XP left to next tier",
                        color = Color(0xFF8EC5FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    val progress = userXp?.let { info ->
                        info.xpTier?.let { tier ->
                            val current = info.userXpAmount.toFloat()
                            val min = tier.minXp.toFloat()
                            val max = tier.maxXp.toFloat()
                            ((current - min) / (max - min) * 100).toInt()
                        }
                    } ?: 0
                    
                    Text(
                        text = "$progress%",
                        color = Color(0xFF8EC5FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                val progressValue = userXp?.let { info ->
                    info.xpTier?.let { tier ->
                        val current = info.userXpAmount.toFloat()
                        val min = tier.minXp.toFloat()
                        val max = tier.maxXp.toFloat()
                        (current - min) / (max - min)
                    }
                } ?: 0f
                
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(top = 8.dp),
                    color = Color(0xFF8EC5FF),
                    trackColor = Color(0xFF8EC5FF).copy(alpha = 0.2f)
                )
            }
            
            // All Tiers Section
            Text(
                text = "All XP Tiers",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // All Tiers Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(xpTiers) { tier ->
                    TierCard(
                        tier = tier,
                        isCurrentTier = tier.id == userXp?.xpTier?.id,
                        currentXp = userXp?.userXpAmount ?: 0
                    )
                }
            }
        }

        // Recent XP Activity Bottom Sheet - MUCH HIGHER
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(topStart = 70.dp, topEnd = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header
                    Text(
                        text = "Recent XP Activity",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 12.dp ,end = 8.dp)
                    )

                    // XP History Items - Scrollable or Empty State
                    if (xpHistory.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(xpHistory) { historyItem ->
                                XpHistoryCard(historyItem = historyItem)
                            }
                        }
                    } else {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Recent Activity",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your XP activities will appear here",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TierCard(
    tier: XpTierResponse,
    isCurrentTier: Boolean,
    currentXp: Long
) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentTier) Color(0xFF8EC5FF) else Color(0xFF3C4C5C)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tier Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when (tier.name.lowercase()) {
                            "bronze" -> Color(0xFFCD7F32)
                            "silver" -> Color(0xFFC0C0C0)
                            "gold" -> Color(0xFFFFD700)
                            "platinum" -> Color(0xFF8E8E93)
                            else -> Color(0xFF6C63FF)
                        }.copy(alpha = if (isCurrentTier) 1f else 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (tier.name.lowercase()) {
                    "bronze" -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = tier.name,
                        tint = if (isCurrentTier) Color.White else Color(0xFFCD7F32)
                    )
                    "silver" -> Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = tier.name,
                        tint = if (isCurrentTier) Color.White else Color(0xFFC0C0C0)
                    )
                    "gold" -> Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = tier.name,
                        tint = if (isCurrentTier) Color.White else Color(0xFFFFD700)
                    )
                    "platinum" -> Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = tier.name,
                        tint = if (isCurrentTier) Color.White else Color(0xFF8E8E93)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tier Name
            Text(
                text = tier.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // XP Range
            Text(
                text = "${tier.minXp} - ${tier.maxXp} XP",
                fontSize = 16.sp,
                color = if (isCurrentTier) Color.White.copy(alpha = 0.8f) else Color(0xFF8EC5FF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Benefits Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenefitItem(
                    title = "Perk Bonus",
                    value = "${tier.perkAmountPercentage}%",
                    isCurrentTier = isCurrentTier
                )
                BenefitItem(
                    title = "XP Multiplier",
                    value = "${tier.xpPerkMultiplier}x",
                    isCurrentTier = isCurrentTier
                )
                BenefitItem(
                    title = "Notification XP",
                    value = "+${tier.xpPerNotification} XP",
                    isCurrentTier = isCurrentTier
                )
                BenefitItem(
                    title = "Promotion XP",
                    value = "+${tier.xpPerPromotion} XP",
                    isCurrentTier = isCurrentTier
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            val progress = if (currentXp >= tier.minXp) {
                (currentXp - tier.minXp).toFloat() / (tier.maxXp - tier.minXp).toFloat()
            } else 0f

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = if (isCurrentTier) Color.White else Color(0xFF8EC5FF),
                trackColor = if (isCurrentTier) Color.White.copy(alpha = 0.3f) else Color(0xFF8EC5FF).copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun BenefitItem(
    title: String,
    value: String,
    isCurrentTier: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = if (isCurrentTier) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isCurrentTier) Color.White else Color(0xFF8EC5FF)
        )
    }
}

@Composable
private fun XpHistoryCard(
    historyItem: XpHistoryDto
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF374151)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // XP Gain Method Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when (historyItem.gainMethod) {
                            XpGainMethod.NOTIFICATION -> Color(0xFF8EC5FF)
                            XpGainMethod.PERK -> Color(0xFF8EC5FF)
                            XpGainMethod.PROMOTION -> Color(0xFF8EC5FF)
                            XpGainMethod.ONBOARDING -> Color(0xFF8EC5FF)

                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (historyItem.gainMethod) {
                    XpGainMethod.NOTIFICATION -> Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notification",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    XpGainMethod.PERK -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Perk",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    XpGainMethod.PROMOTION -> Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = "Promotion",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    XpGainMethod.ONBOARDING -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Onboarding",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Activity Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (historyItem.gainMethod) {
                        XpGainMethod.NOTIFICATION -> "Notification Viewed"
                        XpGainMethod.PERK -> "Perk Redeemed"
                        XpGainMethod.PROMOTION -> "Promotion Used"
                        XpGainMethod.ONBOARDING -> "Onboarding Completed"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Transaction #${historyItem.transactionId}",
                    fontSize = 14.sp,
                    color = Color(0xFF8EC5FF),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // XP Amount
            Text(
                text = "+${historyItem.amount} XP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8EC5FF)
            )
        }
    }
}