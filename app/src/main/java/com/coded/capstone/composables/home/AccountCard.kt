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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.xp.UserXpInfoResponse
import com.coded.capstone.ui.theme.AppTypography

@Composable
fun AccountCard(
    account: AccountResponse,
    onCardClick: (() -> Unit),
    modifier: Modifier = Modifier,
    userXp: UserXpInfoResponse? = null
) {
    // Check if this is a cashback card to apply special styling
    val isCashbackCard = account.accountType?.lowercase() == "cashback"
    
    // Get tier-based colors for cashback cards
    val tierColors = if (isCashbackCard && userXp?.xpTier != null) {
        getTierCardColors(userXp.xpTier.name.lowercase())
    } else {
        getDefaultCardColors(account.accountType?.lowercase())
    }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            tierColors.primary,
            tierColors.secondary
        )
    )

    val accountIcon = when (account.accountType?.lowercase()) {
        "debit" -> Icons.Default.CreditCard
        "credit" -> Icons.Default.Payment
        "cashback" -> getTierIcon(userXp?.xpTier?.name?.lowercase())
        "savings" -> Icons.Default.Savings
        "business" -> Icons.Default.Business
        else -> Icons.Default.AccountBalance
    }

    val accountName = when (account.accountType?.lowercase()) {
        "debit" -> "Debit Card"
        "credit" -> "Credit Card"
        "cashback" -> "${userXp?.xpTier?.name ?: "Cashback"} Card"
        "savings" -> "Savings Account"
        "business" -> "Business Account"
        else -> "Bank Account"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isCashbackCard) 240.dp else 200.dp) // Taller for cashback cards
            .shadow(
                elevation = if (isCashbackCard) 16.dp else 8.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp), // More rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            // Add tier-specific decorative elements for cashback cards
            if (isCashbackCard) {
                TierDecorationOverlay(userXp?.xpTier?.name?.lowercase())
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), // Increased padding
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with account type and icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = accountName,
                            style = AppTypography.headlineSmall,
                            color = tierColors.text,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (isCashbackCard && userXp?.xpTier != null) {
                            Text(
                                text = "${userXp.userXpAmount} XP",
                                style = AppTypography.bodyMedium,
                                color = tierColors.text.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Icon(
                        imageVector = accountIcon,
                        contentDescription = null,
                        tint = tierColors.text.copy(alpha = 0.9f),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Account number
                Column {
                    Text(
                        text = "Account Number",
                        style = AppTypography.bodyMedium,
                        color = tierColors.text.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (!account.accountNumber.isNullOrBlank() && account.accountNumber.length >= 4) {
                            "•••• •••• •••• ${account.accountNumber.takeLast(4)}"
                        } else {
                            "•••• •••• •••• ••••"
                        },
                        style = AppTypography.headlineSmall,
                        color = tierColors.text,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Balance - Main focus
                Column {
                    Text(
                        text = "Available Balance",
                        style = AppTypography.bodyMedium,
                        color = tierColors.text.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${String.format("%.3f", account.balance ?: 0.0)} KWD",
                        style = AppTypography.displaySmall,
                        color = tierColors.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCashbackCard) 32.sp else 28.sp
                    )
                }
                
                // Additional tier info for cashback cards
                if (isCashbackCard && userXp?.xpTier != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cashback Rate",
                            style = AppTypography.bodySmall,
                            color = tierColors.text.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${userXp.xpTier.perkAmountPercentage}%",
                            style = AppTypography.bodyMedium,
                            color = tierColors.accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TierDecorationOverlay(tierName: String?) {
    when (tierName) {
        "bronze" -> {
            // Bronze decorative elements
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 250.dp, y = (-20).dp)
                    .background(
                        Color(0xFFCD7F32).copy(alpha = 0.1f),
                        CircleShape
                    )
            )
        }
        "silver" -> {
            // Silver decorative elements
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 260.dp, y = 10.dp)
                    .background(
                        Color(0xFFC0C0C0).copy(alpha = 0.15f),
                        CircleShape
                    )
            )
        }
        "gold" -> {
            // Gold decorative elements
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 240.dp, y = (-30).dp)
                    .background(
                        Color(0xFFFFD700).copy(alpha = 0.2f),
                        CircleShape
                    )
            )
        }
        "platinum" -> {
            // Platinum decorative elements
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .offset(x = 270.dp, y = 20.dp)
                    .background(
                        Color(0xFFE5E4E2).copy(alpha = 0.2f),
                        CircleShape
                    )
            )
        }
    }
}

private fun getTierCardColors(tierName: String): CardColors {
    return when (tierName) {
        "bronze" -> CardColors(
            primary = Color(0xFF8B4513),
            secondary = Color(0xFFCD7F32),
            text = Color.White,
            accent = Color(0xFFFFE4B5)
        )
        "silver" -> CardColors(
            primary = Color(0xFF708090),
            secondary = Color(0xFFC0C0C0),
            text = Color.White,
            accent = Color(0xFFE6E6FA)
        )
        "gold" -> CardColors(
            primary = Color(0xFFB8860B),
            secondary = Color(0xFFFFD700),
            text = Color.White,
            accent = Color(0xFFFFFACD)
        )
        "platinum" -> CardColors(
            primary = Color(0xFF2F4F4F),
            secondary = Color(0xFF696969),
            text = Color.White,
            accent = Color(0xFFF0F8FF)
        )
        else -> getDefaultCardColors("cashback")
    }
}

private fun getDefaultCardColors(accountType: String?): CardColors {
    return when (accountType) {
        "debit" -> CardColors(
            primary = Color(0xFF192234),
            secondary = Color(0xFF030505),
            text = Color.White,
            accent = Color(0xFF8EC5FF)
        )
        "credit" -> CardColors(
            primary = Color(0xFF1D2A31),
            secondary = Color(0xFF12121E),
            text = Color.White,
            accent = Color(0xFF8EC5FF)
        )
        "cashback" -> CardColors(
            primary = Color(0xFF636B69),
            secondary = Color(0xFF1C1B1B),
            text = Color.White,
            accent = Color(0xFF8EC5FF)
        )
        else -> CardColors(
            primary = Color(0xFF050303),
            secondary = Color(0xFF102D49),
            text = Color.White,
            accent = Color(0xFF8EC5FF)
        )
    }
}

private fun getTierIcon(tierName: String?): ImageVector {
    return when (tierName) {
        "bronze" -> Icons.Default.Star
        "silver" -> Icons.Default.FavoriteBorder
        "gold" -> Icons.Default.Favorite
        "platinum" -> Icons.Default.Diamond
        else -> Icons.Default.Redeem
    }
}

// Enhanced data class for card colors with accent color
private data class CardColors(
    val primary: Color,
    val secondary: Color,
    val text: Color,
    val accent: Color = Color(0xFF8EC5FF)
)