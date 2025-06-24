package com.coded.capstone.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import java.math.BigDecimal
import kotlin.random.Random
import com.coded.capstone.ui.theme.AppTypography

@Composable
fun RewardCard(
    account: AccountResponse,
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

    val glassShape: Shape = RectangleShape // No rounded edges

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = glassShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Blurred glass background with bleeding effect
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(50.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.18f), // Center: more opaque
                                Color.White.copy(alpha = 0.01f)  // Edge: almost transparent
                            ),
                            center = Offset(250f, 90f), // Center of the card, adjust as needed
                            radius = 500f
                        ),
                        shape = glassShape
                    )
            )
            // Card content (not blurred)
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
                                tier.first().toString(),
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }
                        Text(
                            "$tier Tier",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    // Balance info
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF8EC5FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Cashback",
                                fontSize = 20.sp,
                                style = AppTypography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Text(
                            text = "KD $cashbackAmount",
                            color = Color.White,
                            fontSize = 35.sp,
                            style = AppTypography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Points and Redeem Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Points info
                    Text(
                        "$points Points",
                        color = Color.White.copy(alpha = 0.9f),
                        style = AppTypography.bodySmall
                    )
                    // Redeem button (underlined, no background)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (nextTierPoints > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "$nextTierPoints to next tier",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = AppTypography.bodySmall
                                )
                            }
                        }
                        Text(
                            text = "Redeem",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { onClick() }
                                .padding(start = 8.dp),
                            style = AppTypography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                        )
                    }
                }
            }
        }
    }
}
