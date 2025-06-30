package com.coded.capstone.composables.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.SVG.BagHeartFillIcon
import com.coded.capstone.SVG.BaselineShoppingBasketIcon
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.respositories.AccountProductRepository
import com.coded.capstone.SVG.RoundDiamondIcon
import com.coded.capstone.SVG.QueenCrownIcon
import com.coded.capstone.SVG.CoinsIcon
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.SVG.ElectricalEnergyFilledIcon
import com.coded.capstone.SVG.SharpStarsIcon
import com.coded.capstone.SVG.RoundBusinessCenterIcon
import com.coded.capstone.SVG.StarFourFillIcon

@Composable
fun WalletCard(
    account: AccountResponse,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    tiltAngle: Float = 0f,
    scale: Float = 1f,
    alpha: Float = 1f,
    recommendationType: String? = null,
    showDetails: Boolean = true
) {
    // Get account product details
    val accountProduct = AccountProductRepository.accountProducts.find {
        it.id == account.accountProductId
    }

    val bankName = accountProduct?.name ?: "Bank"
    val cardType = account.accountType?.uppercase() ?: "ACCOUNT"

    // Modern premium card gradients based on account type and recommendation type
    val cardGradient = when {
        // Special card cases - these take priority over recommendation types
        bankName.lowercase().contains("diamond") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF091221), // Navy 900
                Color(0xFF0C1628), // Navy 800
                Color(0xFF070E1A), // Navy 700
                Color(0xFF070E1A)  // Navy 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        bankName.lowercase().contains("platinum") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF6B7280), // Silver 500
                Color(0xFF9CA3AF), // Silver 400
                Color(0xFF4B5563), // Silver 600
                Color(0xFF6B7280)  // Silver 500
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        bankName.lowercase().contains("cashback") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4EC5F5), // Amber 600
                Color(0xFF7AD1F5), // Amber 500
                Color(0xFF22ACE3), // Amber 700
                Color(0xFF22ACE3)  // Amber 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        bankName.lowercase().contains("shopping") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF6B1D45), // Red 600
                Color(0xFF9F3B70), // Red 500
                Color(0xFF501233), // Red 700
                Color(0xFF4F1332)  // Red 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        bankName.lowercase().contains("salary") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF1E113D), // Blue 700
                Color(0xFF431952), // Blue 500
                Color(0xFF190E33), // Blue 600
                Color(0xFF190E33)  // Blue 700
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        bankName.lowercase().contains("business pro") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3730A3), // Indigo 900
                Color(0xFF639CF1), // Indigo 500
                Color(0xFF4338CA), // Indigo 700
                Color(0xFF3730A3)  // Indigo 900
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        // Youth Starter card special case
        bankName.lowercase().contains("youth starter") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF651351), // Violet 600
                Color(0xFF8D3077), // Violet 500
                Color(0xFF2C1365), // Violet 700
                Color(0xFF2C1365)  // Violet 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        // Shopper's Delight card special case
        bankName.lowercase().contains("shopper's delight") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF520607), // Red 600
                Color(0xFF6E0C0D), // Red 500
                Color(0xFF440506), // Red 700
                Color(0xFF440506)  // Red 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        // Lifestyle Premium card special case
        bankName.lowercase().contains("lifestyle premium") -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF222B65), // Amber 600
                Color(0xFF3B5E93), // Amber 500
                Color(0xFF1F173A), // Amber 700
                Color(0xFF1F173A)  // Amber 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        // Special recommendation cards
        recommendationType?.lowercase() == "travel" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF7AF380), // Blue 800
                Color(0xFFA5F5A9), // Blue 500
                Color(0xFF136870), // Blue 700
                Color(0xFF136870)  // Blue 800
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        recommendationType?.lowercase() == "family essentials" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF49899F), // Emerald 600
                Color(0xFF80D1EC), // Emerald 500
                Color(0xFF115F79), // Emerald 700
                Color(0xFF115F79)  // Emerald 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        recommendationType?.lowercase() == "youth starter" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF651351), // Violet 600
                Color(0xFF8D3077), // Violet 500
                Color(0xFF2C1365), // Violet 700
                Color(0xFF2C1365)  // Violet 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        recommendationType?.lowercase() == "dining" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFD97706), // Amber 600
                Color(0xFFF59E0B), // Amber 500
                Color(0xFFB45309), // Amber 700
                Color(0xFFD97706)  // Amber 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        recommendationType?.lowercase() == "health" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF600612), // Cyan 600
                Color(0xFF861020), // Cyan 500
                Color(0xFF600612), // Cyan 700
                Color(0xFF600612)  // Cyan 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        recommendationType?.lowercase() == "education" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF219406), // Orange 800
                Color(0xFF8CC241), // Orange 600
                Color(0xFF219406), // Orange 700
                Color(0xFF219406)  // Orange 800
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        // Regular account type cards
        account.accountType?.lowercase() == "debit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF132138), // Slate 800
                Color(0xFF263D64), // Slate 700
                Color(0xFF0A121F), // Slate 600
                Color(0xFF132138)  // Slate 500

            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        account.accountType?.lowercase() == "credit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF16191A), // Red 800
                Color(0xFF343A3B), // Red 800
                Color(0xFF000000), // Red 600
                Color(0xFF16191A)  // Red 500

            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        account.accountType?.lowercase() == "savings" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4E5454), // Green 900
                Color(0xFF818A8A), // Green 800
                Color(0xFF2F3333), // Green 600
                Color(0xFF4E5454)  // Green 500
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        account.accountType?.lowercase() == "business" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3730a3), // Indigo 900
                Color(0xFF6862C7), // Indigo 800
                Color(0xFF201F5B), // Indigo 600
                Color(0xFF3730a3)  // Indigo 500
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF384349), // Gray 700
                Color(0xFF58656C), // Gray 600
                Color(0xFF273034), // Gray 500
                Color(0xFF384349)  // Gray 400
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
    }

    // Get recommendation icon
    val recommendationIcon = when {
        bankName.lowercase().contains("diamond") -> null // Will use RoundDiamondIcon instead
        bankName.lowercase().contains("platinum") -> null // Will use QueenCrownIcon instead
        bankName.lowercase().contains("cashback") -> null // Will use CoinsIcon instead
        bankName.lowercase().contains("shopping") -> null // Will use CardTransferBoldIcon instead
        bankName.lowercase().contains("salary") -> null // Will use SharpStarsIcon instead
        bankName.lowercase().contains("business pro") -> null // Will use RoundBusinessCenterIcon instead
        bankName.lowercase().contains("youth starter") -> null
        bankName.lowercase().contains("shopper's delight") -> null
        bankName.lowercase().contains("lifestyle premium") -> null
        recommendationType?.lowercase() == "travel" -> Icons.Default.Flight
        recommendationType?.lowercase() == "family essentials" -> Icons.Default.FamilyRestroom
        recommendationType?.lowercase() == "entertainment" -> Icons.Default.Movie
        recommendationType?.lowercase() == "shopping" -> Icons.Default.ShoppingCart
        recommendationType?.lowercase() == "dining" -> Icons.Default.Restaurant
        recommendationType?.lowercase() == "health" -> Icons.Default.LocalHospital
        recommendationType?.lowercase() == "education" -> Icons.Default.School
        else -> null
    }

    // Get recommendation label
    val recommendationLabel = when (recommendationType?.lowercase()) {
        "travel" -> "TRAVEL"
        "family essentials" -> "FAMILY"
        "entertainment" -> "ENTERTAINMENT"
        "shopping" -> "SHOPPING"
        "dining" -> "DINING"
        "health" -> "HEALTH"
        "education" -> "EDUCATION"
        else -> null // Don't show recommendation label, show account type instead
    }

    val isProductCard = !showDetails

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                rotationZ = tiltAngle
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
        ) {
            // Subtle geometric pattern overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.03f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.02f)
                            ),
                            radius = 400f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Account type and contactless
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Account type (moved from bottom)
                    Text(
                        text = cardType,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // Recommendation icon or contactless payment icon
                    if (bankName.lowercase().contains("diamond")) {
                        RoundDiamondIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else if (bankName.lowercase().contains("platinum")) {
                        QueenCrownIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else if (bankName.lowercase().contains("cashback")) {
                        CoinsIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else if (bankName.lowercase().contains("shopping")) {
                        BaselineShoppingBasketIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else if (bankName.lowercase().contains("salary")) {
                        SharpStarsIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else if (bankName.lowercase().contains("business pro")) {
                        RoundBusinessCenterIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }else if (bankName.lowercase().contains("youth delight")) {
                        ElectricalEnergyFilledIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else if (bankName.lowercase().contains("shopper's delight")) {
                        BagHeartFillIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }else if (bankName.lowercase().contains("lifestyle premium")) {
                        StarFourFillIcon(
                            modifier = Modifier.size(28.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }else {
                        Icon(
                            imageVector = recommendationIcon ?: Icons.Default.Contactless,
                            contentDescription = recommendationLabel ?: "Contactless Payment",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Middle section: EMV Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Realistic EMV Chip
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(40.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700), // Gold
                                        Color(0xFFDAA520), // Goldenrod
                                        Color(0xFFB8860B), // Dark goldenrod
                                        Color(0xFFFFD700)  // Gold
                                    )
                                ),
                                RoundedCornerShape(6.dp)
                            )
                            .shadow(2.dp, RoundedCornerShape(6.dp))
                    ) {
                        // Chip contact pattern
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(3) { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    repeat(4) { col ->
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .background(
                                                    Color(0xFF8B4513).copy(alpha = 0.8f),
                                                    RoundedCornerShape(1.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom section: Card details
                Column {
                    if (showDetails) {
                        // Account number
                        Text(
                            text = formatAccountNumber(account.accountNumber),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    // Bottom row: Bank name and balance or just bank name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Bank name (moved from top)
                        Column {
                            Text(
                                text = "BANK NAME",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = bankName.uppercase(),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        if (showDetails) {
                            // Balance
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "BALANCE",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${String.format("%.2f", account.balance.toDouble())} KD",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Premium shine effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(500f, 300f)
                        )
                    )
            )
        }
    }
}

private fun formatAccountNumber(accountNumber: String?): String {
    return if (!accountNumber.isNullOrBlank() && accountNumber.length >= 4) {
        val maskedPart = "•••• •••• •••• "
        val lastFour = accountNumber.takeLast(4)
        maskedPart + lastFour
    } else {
        "•••• •••• •••• ••••"
    }
}
