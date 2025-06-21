package com.coded.capstone.composables.perks

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                .size(64.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.3f),
                            color.copy(alpha = 0.1f)
                        )
                    ),
                    CircleShape
                )
                .padding(16.dp)
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EnhancedPerkItem(perk: PerkDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1D)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Perk icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    getPerkColor(perk.type).copy(alpha = 0.3f),
                                    getPerkColor(perk.type).copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPerkIcon(perk.type),
                        contentDescription = perk.type,
                        tint = getPerkColor(perk.type),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Perk details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = perk.type?.replaceFirstChar { it.uppercase() } ?: "Unknown Perk",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    perk.perkAmount?.let { amount ->
                        Text(
                            text = if (perk.type?.contains("cashback", ignoreCase = true) == true) {
                                "${amount}% Cashback"
                            } else if (perk.type?.contains("discount", ignoreCase = true) == true) {
                                "${amount}% Discount"
                            } else {
                                "Amount: $amount"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = getPerkColor(perk.type),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    perk.minPayment?.let { minPayment ->
                        Text(
                            text = "Min. Payment: $minPayment KWD",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Rewards XP Badge
                perk.rewardsXp?.let { xp ->
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFFFD700).copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+$xp XP",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }

            // Enhanced categories display
            if (!perk.categories.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Applicable Categories:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    perk.categories.take(3).forEach { category ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category.name ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8B5CF6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (perk.categories.size > 3) {
                        Text(
                            text = "+${perk.categories.size - 3} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Tier indicator
            if (perk.isTierBased == true) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF10B981).copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏆 Tier Based Rewards",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun getPerkColor(type: String?): Color {
    return when (type?.lowercase()) {
        "cashback" -> Color(0xFF10B981)
        "discount" -> Color(0xFF8B5CF6)
        "points" -> Color(0xFFFFD700)
        "rewards" -> Color(0xFFEF4444)
        else -> Color(0xFF06B6D4)
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

@Composable
fun ServicesRow(
    onTransfer: () -> Unit,
    onTransferToOthers: () -> Unit,
    onCloseAccount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1D).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            EnhancedServiceButton(
                icon = { CardTransferBoldIcon(modifier = Modifier.size(24.dp)) },
                label = "Transfer",
                color = Color(0xFF8B5CF6),
                onClick = onTransfer
            )
            EnhancedServiceButton(
                icon = { TransferUsersIcon(modifier = Modifier.size(24.dp)) },
                label = "Send",
                color = Color(0xFF10B981),
                onClick = onTransferToOthers
            )
            EnhancedServiceButton(
                icon = { CreditCardCloseIcon(modifier = Modifier.size(24.dp)) },
                label = "Close",
                color = Color(0xFFEF4444),
                onClick = onCloseAccount
            )
        }
    }
}