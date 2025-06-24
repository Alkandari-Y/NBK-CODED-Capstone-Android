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

@Composable
 fun TierProgressSection() {
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
                    text = "Current: Gold", // 🔥 REPLACE: Get current tier from your XP system
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Next: Platinum", // 🔥 REPLACE: Get next tier from your XP system
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = 0.75f, // 🔥 REPLACE: Calculate progress = currentXP / xpNeededForNextTier
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
                    text = "7,500 XP", // 🔥 REPLACE: Get current XP from your system
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "2,500 XP to go", // 🔥 REPLACE: Calculate remaining XP needed
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }




            Spacer(modifier = Modifier.height(12.dp))

            TierRequirementItem(
                text = "Account age: 6+ months", // 🔥 REPLACE: Calculate actual account age
                isCompleted = true // 🔥 REPLACE: Check if user meets age requirement
            )

            Spacer(modifier = Modifier.height(12.dp))

            TierRequirementItem(
                text = "Investment portfolio: \$5,000+", // 🔥 REPLACE: Get actual investment data
                isCompleted = false // 🔥 REPLACE: Check if user meets investment requirement
            )
        }
    }
}