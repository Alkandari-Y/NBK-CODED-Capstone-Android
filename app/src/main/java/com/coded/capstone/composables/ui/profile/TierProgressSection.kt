package com.coded.capstone.composables.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    // Find next tier
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

    // Calculate XP to next tier
    val xpToNextTier = if (nextTier != null && userXp != null) {
        nextTier.minXp - userXp.userXpAmount
    } else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tier Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current and Next Tier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: ${currentTier?.name ?: "Loading"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Next: ${nextTier?.name ?: "Max Tier"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF2C3E50),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // XP Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${userXp?.userXpAmount ?: 0} XP",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium
                )
                if (nextTier != null) {
                    Text(
                        text = "$xpToNextTier XP to go",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // XP Tier Requirements
            TierRequirementItem(
                text = "Min XP: ${currentTier?.minXp ?: 0}",
                isCompleted = userXp?.userXpAmount?.let { it >= (currentTier?.minXp ?: 0) } ?: false
            )

            Spacer(modifier = Modifier.height(12.dp))

            TierRequirementItem(
                text = "Max XP: ${currentTier?.maxXp ?: 0}",
                isCompleted = userXp?.userXpAmount?.let { it >= (currentTier?.maxXp ?: 0) } ?: false
            )
        }
    }
}