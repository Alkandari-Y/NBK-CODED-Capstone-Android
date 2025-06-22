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

@Composable
fun WalletCard(
    account: AccountResponse,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    tiltAngle: Float = 0f,
    scale: Float = 1f,
    alpha: Float = 1f
) {
    // Metallic gradients based on card type
    val cardGradient = when (account.accountType?.lowercase()) {
        "debit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF2D3748), // Dark gray
                Color(0xFF4A5568), // Medium gray
                Color(0xFF718096)  // Light gray
            )
        )
        "credit" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A202C), // Very dark
                Color(0xFF2D3748), // Dark
                Color(0xFF4A5568)  // Medium
            )
        )
        "cashback" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF2B6CB0), // Blue
                Color(0xFF3182CE), // Medium blue
                Color(0xFF4299E1)  // Light blue
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF4A5568),
                Color(0xFF718096),
                Color(0xFFA0AEC0)
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationZ = tiltAngle
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: EMV Chip (left) and Contactless symbol (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // EMV Chip
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(32.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFD4AF37), // Gold
                                        Color(0xFFB8860B), // Dark gold
                                        Color(0xFFD4AF37)  // Gold
                                    )
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    ) {
                        // Chip grid pattern
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(4) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    repeat(6) {
                                        Box(
                                            modifier = Modifier
                                                .size(2.dp)
                                                .background(
                                                    Color.Black.copy(alpha = 0.4f),
                                                    RoundedCornerShape(0.5.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Contactless symbol
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Contactless",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { rotationZ = 90f }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom section: Card number and name (left) and Amount (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Card name and number (bottom left)
                    Column {
                        Text(
                            text = when (account.accountType?.lowercase()) {
                                "debit" -> "NBK INFINITE"
                                "credit" -> "NBK VISA"
                                "cashback" -> "NBK CASHBACK"
                                else -> "NBK ACCOUNT"
                            },
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (!account.accountNumber.isNullOrBlank() && account.accountNumber.length >= 4) {
                                "••••••••••${account.accountNumber.takeLast(4)}"
                            } else {
                                "••••••••••5758"
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }

                    // Amount (bottom right)
                    Text(
                        text = "amount :${String.format("%.0f", account.balance?.toDouble() ?: 0.0)} KD",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Subtle metallic shine effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(400f, 400f)
                        )
                    )
            )
        }
    }
}