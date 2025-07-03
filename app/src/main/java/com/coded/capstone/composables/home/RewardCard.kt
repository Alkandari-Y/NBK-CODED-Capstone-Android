package com.coded.capstone.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.xp.UserXpInfoResponse
import com.coded.capstone.ui.theme.AppTypography

@Composable
fun RewardCard(
    account: AccountResponse,
    userXp: UserXpInfoResponse?,
    onClick: () -> Unit
) {
    val tierName = userXp?.xpTier?.name?.lowercase() ?: "bronze"
    val tierColors = getTierColors(tierName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Reduced height for more compact design
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = tierColors.shadowColor
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Tier-based gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                tierColors.primary,
                                tierColors.secondary
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
            )

            // Minimal decorative element
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 250.dp, y = (-30).dp)
                    .background(
                        tierColors.accentColor.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            // Card content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Tier name on left, XP on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tier name on left
                    Text(
                        text = "${userXp?.xpTier?.name ?: "Bronze"} Cashback",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )

                    // XP on right
                    Text(
                        text = "${userXp?.userXpAmount ?: 0} XP",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold // FIXED: Bold to make it obvious
                    )
                }

                // Balance section - simplified
                Column {
                    Text(
                        text = "${String.format("%.2f", account.balance ?: 0.0)} KWD",
                        color = Color(0xFF8EC5FF), // FIXED: Blue balance same as accounts
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Available Balance",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TierDecorationOverlay(tierName: String) {
    // Removed - using minimal decoration in main component
}

private fun getTierColors(tierName: String): TierColors {
    return when (tierName) {
        "bronze" -> TierColors(
            primary = Color(0xFF8B4513),
            secondary = Color(0xFFCD7F32),
            tertiary = Color(0xFFDEB887),
            accentColor = Color(0xFFFFE4B5),
            shadowColor = Color(0xFFCD7F32)
        )
        "silver" -> TierColors(
            primary = Color(0xFF5C636E),
            secondary = Color(0xFF868686),
            tertiary = Color(0xFFBDBDBD),
            accentColor = Color(0xFFE6E6FA),
            shadowColor = Color(0xFFC0C0C0)
        )
        "gold" -> TierColors(
            primary = Color(0xFFB8860B),
            secondary = Color(0xFFFFD700),
            tertiary = Color(0xFFFFE55C),
            accentColor = Color(0xFFFFFACD),
            shadowColor = Color(0xFFFFD700)
        )
        "platinum" -> TierColors(
            primary = Color(0xFF2F4F4F),
            secondary = Color(0xFF696969),
            tertiary = Color(0xFF778899),
            accentColor = Color(0xFFF0F8FF),
            shadowColor = Color(0xFF696969)
        )
        else -> TierColors(
            primary = Color(0xFF636B69),
            secondary = Color(0xFF1C1B1B),
            tertiary = Color(0xFF2A2A2A),
            accentColor = Color(0xFF8EC5FF),
            shadowColor = Color(0xFF636B69)
        )
    }
}

// REMOVED: getTierIcon function since we don't need icons anymore

private data class TierColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accentColor: Color,
    val shadowColor: Color
)