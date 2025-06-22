package com.coded.capstone.composables.perks

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.SVG.CreditCardCloseIcon
import com.coded.capstone.SVG.TransferUsersIcon
import com.coded.capstone.data.responses.perk.PerkDto

@Composable
fun EnhancedServiceButton(
    icon: @Composable () -> Unit,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .scale(scale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .padding(14.dp)
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color(0xFF374151),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}
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

@Composable
fun CategoryIndicator(
    category: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color(0xFF374151),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Category indicator dot
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = getCategoryColor(category),
                    shape = CircleShape
                )
        )

        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFE5E7EB),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

// Helper function to get category-specific colors
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food", "restaurant", "dining" -> Color(0xFFEF4444)
        "shopping", "retail" -> Color(0xFF8B5CF6)
        "fuel", "gas", "petrol" -> Color(0xFF10B981)
        "travel", "hotel" -> Color(0xFF3B82F6)
        "entertainment" -> Color(0xFFF59E0B)
        "groceries" -> Color(0xFF06B6D4)
        else -> Color(0xFF6B7280)
    }
}

// Keep existing helper functions
fun getPerkColor(type: String?): Color {
    return when (type?.lowercase()) {
        "cashback" -> Color(0xFF059669)
        "discount" -> Color(0xFF7C3AED)
        "points" -> Color(0xFFD97706)
        "rewards" -> Color(0xFFDC2626)
        else -> Color(0xFF2563EB)
    }
}

fun getPerkIcon(type: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type?.lowercase()) {
        "cashback" -> Icons.Default.AttachMoney
        "discount" -> Icons.Default.LocalOffer
        "points", "rewards" -> Icons.Default.Star
        else -> Icons.Default.CardGiftcard
    }
}




//fun getPerkIcon(type: String?): androidx.compose.ui.graphics.vector.ImageVector {
//    return when (type?.lowercase()) {
//        "cashback" -> Icons.Default.AttachMoney
//        "discount" -> Icons.Default.LocalOffer
//        "points", "rewards" -> Icons.Default.Star
//        else -> Icons.Default.CardGiftcard
//    }
//}

