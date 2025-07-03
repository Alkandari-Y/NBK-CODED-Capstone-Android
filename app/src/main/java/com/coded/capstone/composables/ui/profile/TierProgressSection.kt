package com.coded.capstone.composables.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.xp.UserXpInfoResponse
import com.coded.capstone.data.responses.xp.XpTierResponse

@Composable
fun TierProgressSection(
    userXp: UserXpInfoResponse?,
    allTiers: List<XpTierResponse>
) {
    val currentTier = userXp?.xpTier
    val nextTier = if (currentTier != null) {
        allTiers.find { it.minXp > currentTier.maxXp }
    } else null

    // Calculate progress
    val progress = if (currentTier != null && userXp != null) {
        val range = currentTier.maxXp - currentTier.minXp
        val current = userXp.userXpAmount - currentTier.minXp
        (current.toFloat() / range.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val xpToNextTier = if (nextTier != null && userXp != null) {
        nextTier.minXp - userXp.userXpAmount
    } else 0

    // Enhanced tier colors
    val tierColors = getEnhancedTierColors(currentTier?.name?.lowercase())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = tierColors.gradientColors,
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Decorative elements
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 200.dp, y = (-40).dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {
                // Header with tier name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentTier?.name?.uppercase() ?: "LOADING",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = tierColors.primaryTextColor,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "TIER STATUS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = tierColors.secondaryTextColor,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Tier Icon",
                        tint = tierColors.iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Next tier info
                if (nextTier != null) {
                    Text(
                        text = "NEXT: ${nextTier.name?.uppercase()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tierColors.secondaryTextColor,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // FIXED: Clean progress bar - no dark circle indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(
                            Color.White.copy(alpha = 0.25f), // Transparent unfilled
                            RoundedCornerShape(6.dp)
                        )
                ) {
                    // FIXED: Simple white progress fill, no indicator circle
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                Color.White, // Clean white progress
                                RoundedCornerShape(6.dp)
                            )
                    )
                    // REMOVED: No more dark circle indicator
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FIXED: Current XP and XP to go - SAME FONT SIZE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "${userXp?.userXpAmount ?: 0}",
                            fontSize = 24.sp, // FIXED: Same size as "XP to go"
                            fontWeight = FontWeight.Black,
                            color = tierColors.primaryTextColor
                        )
                        Text(
                            text = "CURRENT XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = tierColors.secondaryTextColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (nextTier != null) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "$xpToNextTier",
                                fontSize = 24.sp, // FIXED: Same size as "Current XP"
                                fontWeight = FontWeight.Black, // FIXED: Same weight
                                color = tierColors.primaryTextColor
                            )
                            Text(
                                text = "XP TO GO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = tierColors.secondaryTextColor,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Min/Max XP horizontal layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MIN ${currentTier?.minXp ?: 0} XP",
                        fontSize = 12.sp,
                        color = tierColors.secondaryTextColor,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp
                    )
                    Text(
                        text = "MAX ${currentTier?.maxXp ?: 0} XP",
                        fontSize = 12.sp,
                        color = tierColors.secondaryTextColor,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }
    }
}

// Enhanced tier colors (same as before)
private fun getEnhancedTierColors(tierName: String?): EnhancedTierColors {
    return when (tierName) {
        "bronze" -> EnhancedTierColors(
            gradientColors = listOf(Color(0xFF8B4513), Color(0xFFCD7F32), Color(0xFFDEB887)),
            primaryTextColor = Color.White,
            secondaryTextColor = Color.White.copy(alpha = 0.8f),
            iconColor = Color.White
        )
        "silver" -> EnhancedTierColors(
            gradientColors = listOf(Color(0xFF708090), Color(0xFFC0C0C0), Color(0xFFE6E6FA)),
            primaryTextColor = Color(0xFF2A2A2A),
            secondaryTextColor = Color(0xFF2A2A2A).copy(alpha = 0.7f),
            iconColor = Color(0xFF2A2A2A)
        )
        "gold" -> EnhancedTierColors(
            gradientColors = listOf(Color(0xFFB8860B), Color(0xFFFFD700), Color(0xFFFFFACD)),
            primaryTextColor = Color(0xFF2A2A2A),
            secondaryTextColor = Color(0xFF2A2A2A).copy(alpha = 0.7f),
            iconColor = Color(0xFF2A2A2A)
        )
        "platinum" -> EnhancedTierColors(
            gradientColors = listOf(Color(0xFF2F4F4F), Color(0xFF696969), Color(0xFF778899)),
            primaryTextColor = Color.White,
            secondaryTextColor = Color.White.copy(alpha = 0.8f),
            iconColor = Color.White
        )
        else -> EnhancedTierColors(
            gradientColors = listOf(Color(0xFF636B69), Color(0xFF8EC5FF), Color(0xFFB8E6FF)),
            primaryTextColor = Color.White,
            secondaryTextColor = Color.White.copy(alpha = 0.8f),
            iconColor = Color.White
        )
    }
}

private data class EnhancedTierColors(
    val gradientColors: List<Color>,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val iconColor: Color
)