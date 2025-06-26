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
    alpha: Float = 1f
) {
    // Get account product details
    val accountProduct = AccountProductRepository.accountProducts.find {
        it.id == account.accountProductId
    }

    val bankName = accountProduct?.name ?: "Bank"
    val cardType = account.accountType?.uppercase() ?: "ACCOUNT"

    // Modern premium card gradients based on account type
    val cardGradient = when (account.accountType?.lowercase()) {
        "debit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF132138), // Slate 800
                Color(0xFF263D64), // Slate 700
                Color(0xFF0A121F), // Slate 600
                Color(0xFF132138)  // Slate 500
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "credit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF16191A), // Red 800
                Color(0xFF343A3B), // Red 800
                Color(0xFF000000), // Red 600
                Color(0xFF16191A)  // Red 500
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "savings" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4E5454), // Green 900
                Color(0xFF818A8A), // Green 800
                Color(0xFF2F3333), // Green 600
                Color(0xFF4E5454)  // Green 500
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "business" -> Brush.linearGradient(
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

                    // Contactless payment icon
                    Icon(
                        imageVector = Icons.Default.Contactless,
                        contentDescription = "Contactless Payment",
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
                    // Account number
                    Text(
                        text = formatAccountNumber(account.accountNumber),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom row: Card type and balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Account type
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