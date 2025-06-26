package com.coded.capstone.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.xp.UserXpInfoResponse
import com.coded.capstone.ui.theme.AppTypography
import java.math.BigDecimal
import kotlin.random.Random

@Composable
fun RewardCard(
    account: AccountResponse,
    userXp: UserXpInfoResponse?,
    onClick: () -> Unit
) {

    // Generate stable reward data based on account ID to prevent constant changes
    val points = remember(account.id) { 
        // Use account ID as seed for consistent random generation
        Random(account.id.toLong()).nextInt(500, 2500)
    }
    val cashbackAmount = remember(account.balance) { 
        (account.balance * BigDecimal("0.02")).setScale(2, java.math.RoundingMode.HALF_UP) 
    }
    val tier = remember(points) {
        when {
            points > 2000 -> "Platinum"
            points > 1500 -> "Gold"
            points > 1000 -> "Silver"
            else -> "Bronze"
        }
    }
    val nextTierPoints = remember(points, tier) {
        when (tier) {
            "Bronze" -> 1000 - points
            "Silver" -> 1500 - points
            "Gold" -> 2000 - points
            else -> 0
        }
    }

    val glassShape: Shape = RoundedCornerShape(24.dp) 


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = glassShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // White background
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White, shape = glassShape)
            )
            // Card content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Row: Tier and Balance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tier info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                userXp?.xpTier?.name?.firstOrNull()?.toString() ?: "?",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }
                        Text(
                            "${userXp?.xpTier?.name ?: "Loading"} Tier",
                            color = Color(0xFF23272E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    // XP info
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF8EC5FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "XP",
                                fontSize = 20.sp,
                                style = AppTypography.bodySmall,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Text(
                            text = "${userXp?.userXpAmount ?: 0}",
                            color = Color(0xFF23272E),
                            fontSize = 35.sp,
                            style = AppTypography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Points and Next Tier Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Min XP info
                    Text(
                        "Min XP: ${userXp?.xpTier?.minXp ?: 0}",
                        color = Color(0xFF6B7280),
                        style = AppTypography.bodySmall
                    )
                    // Max XP info
                    Text(
                        "Max XP: ${userXp?.xpTier?.maxXp ?: 0}",
                        color = Color(0xFF6B7280),
                        style = AppTypography.bodySmall
                    )
                }
            }
        }
    }
}
