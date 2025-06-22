package com.coded.capstone.composables.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coded.capstone.composables.perks.CategoryIndicator
import com.coded.capstone.composables.perks.getPerkColor
import com.coded.capstone.composables.perks.getPerkIcon
import com.coded.capstone.data.responses.perk.PerkDto
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun DarkEnhancedPerkItem(perk: PerkDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Simple icon
                Icon(
                    imageVector = getPerkIcon(perk.type),
                    contentDescription = perk.type,
                    tint = getPerkColor(perk.type),
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Perk info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = perk.type?.replaceFirstChar { it.uppercase() } ?: "Perk",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    perk.perkAmount?.let { amount ->
                        Text(
                            text = when {
                                perk.type?.contains("cashback", ignoreCase = true) == true -> "$amount% Back"
                                perk.type?.contains("discount", ignoreCase = true) == true -> "$amount% Off"
                                else -> "$amount Points"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = getPerkColor(perk.type),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // XP Badge (simplified)
                perk.rewardsXp?.let { xp ->
                    Text(
                        text = "$xp XP",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981),
                        modifier = Modifier
                            .background(
                                color = Color(0xFF065F46),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Categories Section (Prominent)
            if (!perk.categories.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))


                // Category Grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    perk.categories.chunked(2).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { category ->
                                CategoryIndicator(
                                    category = category.name ?: "Unknown",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Fill remaining space if odd number
                            if (rowCategories.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                    }
                    // Bottom info row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Min payment
                        perk.minPayment?.let { minPayment ->
                            Text(
                                text = "Min. $minPayment KWD",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9CA3AF)
                            )
                        } ?: Spacer(modifier = Modifier.width(1.dp))

                    }
                }
            }


        }
    }
}