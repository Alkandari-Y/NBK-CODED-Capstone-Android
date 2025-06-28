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
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.respositories.AccountProductRepository

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
        recommendationType?.lowercase() == "entertainment" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF651351), // Violet 600
                Color(0xFF8D3077), // Violet 500
                Color(0xFF2C1365), // Violet 700
                Color(0xFF2C1365)  // Violet 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        recommendationType?.lowercase() == "shopping" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF6B1D45), // Red 600
                Color(0xFF9F3B70), // Red 500
                Color(0xFF501233), // Red 700
                Color(0xFF4F1332)  // Red 600
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
    val recommendationIcon = when (recommendationType?.lowercase()) {
        "travel" -> Icons.Default.Flight
        "family essentials" -> Icons.Default.FamilyRestroom
        "entertainment" -> Icons.Default.Movie
        "shopping" -> Icons.Default.ShoppingCart
        "dining" -> Icons.Default.Restaurant
        "health" -> Icons.Default.LocalHospital
        "education" -> Icons.Default.School
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
                // Top section: Bank name and contactless
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Bank/Product name
                    Text(
                        text = bankName.uppercase(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // Recommendation icon or contactless payment icon
                    Icon(
                        imageVector = recommendationIcon ?: Icons.Default.Contactless,
                        contentDescription = recommendationLabel ?: "Contactless Payment",
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
                    // Bottom row: Card type and balance or just type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Account type or recommendation type
                        Column {
                            Text(
                                text = "ACCOUNT TYPE",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = cardType,
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
