package com.coded.capstone.screens.xp

import androidx.compose.foundation.border
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // Filter state
    var selectedFilter by remember { mutableStateOf<XpGainMethod?>(null) }

    // Filter history based on selected filter
    val filteredHistory = if (selectedFilter != null) {
        xpHistory.filter { it.gainMethod == selectedFilter }
    } else {
        xpHistory
    }

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
        // FIXED HEADER - Consistent with other screens
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF23272E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                // FIXED: More spacing from top
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // FIXED: Clean back button - no circle background
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF8EC5FF), // Consistent blue shade
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // FIXED: Properly centered title
                    Text(
                        text = "XP History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // CONTENT - Clean scrollable column
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // SIMPLE XP OVERVIEW - Clean and compact
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${userXp?.userXpAmount ?: 0} XP",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8EC5FF)
                            )
                            Text(
                                text = "${userXp?.xpTier?.name ?: "Loading"} Tier",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280)
                            )

                            val xpLeft = userXp?.let { info ->
                                info.xpTier?.let { tier ->
                                    tier.maxXp.toLong() - info.userXpAmount
                                }
                            } ?: 0L

                            // Show XP left to next tier
                            if (xpLeft > 0) {
                                Text(
                                    text = "${xpLeft} XP to next tier",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Tier icon with color
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = when (userXp?.xpTier?.name?.lowercase()) {
                                        "silver" -> Color(0xFFC0C0C0)
                                        "gold" -> Color(0xFFFFD700)
                                        "platinum" -> Color(0xFF6B7280)
                                        else -> Color(0xFF8EC5FF)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Tier",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // THREE TIER CARDS - FIXED CENTERING
            item {
                Column {
                    Text(
                        text = "Tier Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF23272E),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        xpTiers.take(3).forEach { tier ->
                            val isCurrentTier = tier.id == userXp?.xpTier?.id
                            val isUnlocked = (userXp?.userXpAmount ?: 0L) >= tier.minXp.toLong()

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(140.dp) // Fixed height for consistency
                                    .then(
                                        if (isCurrentTier) {
                                            Modifier.border(
                                                2.dp,
                                                Color(0xFF8EC5FF),
                                                RoundedCornerShape(16.dp)
                                            )
                                        } else {
                                            Modifier.border(
                                                1.dp,
                                                Color(0xFFE5E7EB),
                                                RoundedCornerShape(16.dp)
                                            )
                                        }
                                    ),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                // FIXED: Use Box with proper centering
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Active indicator (top-right corner)
                                    if (isCurrentTier) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(Color(0xFF8EC5FF), CircleShape)
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                        )
                                    }

                                    // FIXED: Centered content column
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        // Colored tier icon
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = when (tier.name.lowercase()) {
                                                        "silver" -> Color(0xFFC0C0C0)
                                                        "gold" -> Color(0xFFFFD700)
                                                        "platinum" -> Color(0xFF6B7280)
                                                        else -> Color(0xFF8EC5FF)
                                                    },
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = tier.name,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Tier name - centered
                                        Text(
                                            text = tier.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF23272E),
                                            textAlign = TextAlign.Center
                                        )

                                        // XP range - centered
                                        Text(
                                            text = "${tier.minXp}-${if(tier.maxXp.toInt() == 999999) "∞" else tier.maxXp}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF6B7280),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Status - centered
                                        Text(
                                            text = when {
                                                isCurrentTier -> "Current"
                                                isUnlocked -> "Unlocked"
                                                else -> "Locked"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = when {
                                                isCurrentTier -> Color(0xFF8EC5FF)
                                                isUnlocked -> Color(0xFF10B981)
                                                else -> Color(0xFF6B7280)
                                            },
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // RECENT XP ACTIVITY with FILTER CHIPS
            item {
                Column {
                    Text(
                        text = "Recent XP Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF23272E),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    // FILTER CHIPS
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        // All filter
                        item {
                            FilterChip(
                                onClick = { selectedFilter = null },
                                label = {
                                    Text(
                                        "All",
                                        fontSize = 12.sp
                                    )
                                },
                                selected = selectedFilter == null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8EC5FF),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF6B7280)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedFilter == null,
                                    borderColor = Color(0xFF6B7280),
                                    selectedBorderColor = Color(0xFF8EC5FF)
                                )
                            )
                        }

                        // Specific filters
                        val filters = listOf(
                            XpGainMethod.ONBOARDING to "Onboarding",
                            XpGainMethod.NOTIFICATION to "Notifications",
                            XpGainMethod.PERK to "Perks",
                            XpGainMethod.PROMOTION to "Promotions"
                        )

                        items(filters) { (method, label) ->
                            FilterChip(
                                onClick = {
                                    selectedFilter = if (selectedFilter == method) null else method
                                },
                                label = {
                                    Text(
                                        label,
                                        fontSize = 12.sp
                                    )
                                },
                                selected = selectedFilter == method,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8EC5FF),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF6B7280)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedFilter == method,
                                    borderColor = Color(0xFF6B7280),
                                    selectedBorderColor = Color(0xFF8EC5FF)
                                )
                            )
                        }
                    }

                    if (filteredHistory.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredHistory.take(5).forEach { historyItem ->
                                XpHistoryCard(historyItem = historyItem)
                            }
                        }
                    } else {
                        // Empty state
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF8EC5FF).copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (selectedFilter != null) "No ${
                                        when (selectedFilter) {
                                            XpGainMethod.ONBOARDING -> "Onboarding"
                                            XpGainMethod.NOTIFICATION -> "Notification"
                                            XpGainMethod.PERK -> "Perk"
                                            XpGainMethod.PROMOTION -> "Promotion"
                                            else -> ""
                                        }
                                    } Activity" else "No Recent Activity",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF23272E),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (selectedFilter != null) "Try selecting a different filter" else "Your XP activities will appear here",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
            }
        }
    }
}

@Composable
private fun XpHistoryCard(
    historyItem: XpHistoryDto
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // FIXED: Centered icon container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color(0xFF8EC5FF).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (historyItem.gainMethod) {
                        XpGainMethod.NOTIFICATION -> Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(20.dp)
                        )
                        XpGainMethod.PERK -> Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Perk",
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(20.dp)
                        )
                        XpGainMethod.PROMOTION -> Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = "Promotion",
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(20.dp)
                        )
                        XpGainMethod.ONBOARDING -> Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Onboarding",
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Activity info
                Column {
                    Text(
                        text = when (historyItem.gainMethod) {
                            XpGainMethod.NOTIFICATION -> "Notification Viewed"
                            XpGainMethod.PERK -> "Perk Redeemed"
                            XpGainMethod.PROMOTION -> "Promotion Used"
                            XpGainMethod.ONBOARDING -> "Onboarding Completed"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF23272E)
                    )
                    Text(
                        text = "Transaction #${historyItem.transactionId}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // FIXED: Right-aligned XP amount
            Text(
                text = "+${historyItem.amount} XP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981), // Green for positive XP
                textAlign = TextAlign.End
            )
        }
    }
}