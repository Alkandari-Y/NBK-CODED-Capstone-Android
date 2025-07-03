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

    // Function to determine recommendation type based on product name
    fun getRecommendationType(bankName: String): String? {
        return when {
            // Specific product names
            bankName.lowercase().contains("cashback") -> "retail"
            bankName.lowercase().contains("shopping") -> "retail"
            bankName.lowercase().contains("diamond") -> "fashion"
            bankName.lowercase().contains("platinum") -> "wholesale"
            bankName.lowercase().contains("salary") -> "education"
            bankName.lowercase().contains("business pro") -> "technology"
            bankName.lowercase().contains("youth starter") -> "entertainment"
            bankName.lowercase().contains("shopper's delight") -> "retail"
            bankName.lowercase().contains("lifestyle premium") -> "fashion"

            // General category names
            bankName.lowercase().contains("retail") -> "retail"
            bankName.lowercase().contains("travel") -> "travel"
            bankName.lowercase().contains("dining") -> "dining"
            bankName.lowercase().contains("fashion") -> "fashion"
            bankName.lowercase().contains("technology") -> "technology"
            bankName.lowercase().contains("hospitality") -> "hospitality"
            bankName.lowercase().contains("education") -> "education"
            bankName.lowercase().contains("entertainment") -> "entertainment"
            bankName.lowercase().contains("personal care") -> "personal care"
            bankName.lowercase().contains("wholesale") -> "wholesale"

            // Fallback based on account type
            account.accountType?.lowercase() == "credit" -> "retail"
            account.accountType?.lowercase() == "savings" -> "hospitality"
            account.accountType?.lowercase() == "debit" -> "travel"
            account.accountType?.lowercase() == "business" -> "technology"
            else -> "retail" // Default recommendation type
        }
    }

    // Use passed recommendationType or determine from bankName
    val effectiveRecommendationType = recommendationType ?: getRecommendationType(bankName)

    // CONSISTENT CARD GRADIENTS - Single source of truth matching CardSuggestedOnBoarding
    val cardGradient = when (effectiveRecommendationType?.lowercase()) {
        "retail" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3B4B6B), // Lighter muted navy - still premium but more visible
                Color(0xFF4A5A7A),
                Color(0xFF2F3F5F),
                Color(0xFF3B4B6B)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "travel" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3F4A56), // Lighter muted charcoal - better for stacks
                Color(0xFF4B5663),
                Color(0xFF353E4A),
                Color(0xFF3F4A56)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "dining" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3D2B1F), // Deep warm brown - restaurant warmth & appetite
                Color(0xFF4A3529),
                Color(0xFF2F1F15),
                Color(0xFF3D2B1F)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "fashion" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4A3B47), // Sophisticated mauve - luxury fashion elegance
                Color(0xFF564753),
                Color(0xFF3E2F3B),
                Color(0xFF4A3B47)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "technology" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3C3F4F), // Lighter muted blue-purple - tech but not gloomy
                Color(0xFF484B5B),
                Color(0xFF323543),
                Color(0xFF3C3F4F)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "hospitality" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF0F1419), // Almost black with green undertones - luxury hospitality
                Color(0xFF1A1F1A),
                Color(0xFF0D1117),
                Color(0xFF0F1419)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "education" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3D4C72), // Lighter academic blue - more visible in stacks
                Color(0xFF495882),
                Color(0xFF334062),
                Color(0xFF3D4C72)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "entertainment" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4A2C3A), // Deep wine/burgundy - theater & entertainment elegance
                Color(0xFF563846),
                Color(0xFF3E202E),
                Color(0xFF4A2C3A)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "personal care" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3F4A3C), // Sophisticated sage green - spa & wellness
                Color(0xFF4B5648),
                Color(0xFF333E30),
                Color(0xFF3F4A3C)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        "wholesale" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4A5568), // Lighter professional gray - better visibility
                Color(0xFF556175),
                Color(0xFF3E495B),
                Color(0xFF4A5568)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )

        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF3B4B6B), // Default to retail colors
                Color(0xFF4A5A7A),
                Color(0xFF2F3F5F),
                Color(0xFF3B4B6B)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
    }

    // Get appropriate icon based on recommendation type
    val recommendationIcon = when (effectiveRecommendationType?.lowercase()) {
        "travel" -> Icons.Default.Flight
        "hospitality" -> Icons.Default.Hotel
        "entertainment" -> Icons.Default.Movie
        "retail" -> Icons.Default.ShoppingCart
        "dining" -> Icons.Default.Restaurant
        "personal care" -> Icons.Default.LocalHospital
        "education" -> Icons.Default.School
        "technology" -> Icons.Default.Computer
        "fashion" -> Icons.Default.Checkroom
        "wholesale" -> Icons.Default.Business
        else -> Icons.Default.Contactless
    }

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
                // Top section: Account type and icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Account type
                    Text(
                        text = cardType,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // Consistent icon based on recommendation type
                    Icon(
                        imageVector = recommendationIcon,
                        contentDescription = effectiveRecommendationType ?: "Card",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(28.dp)
                    )
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
                        // Bank name
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