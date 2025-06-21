package com.coded.capstone.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.wallet.data.WalletAccountDisplayModel
import com.coded.capstone.data.enums.AccountType
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletCard(
    card: WalletAccountDisplayModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate gradient based on product name and account type
    val nameHash = card.accountProductName.hashCode()
    val colorVariant = abs(nameHash) % 3 // 3 per type

    val cardGradient = when (card.accountType) {
        AccountType.CREDIT -> when (colorVariant) {
            0 -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1A202C), Color(0xFF242F3A), Color(0xFF1B212C),
                    Color(0xFF2D3748), Color(0xFF1A202C)
                )
            )
            1 -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF352E40), Color(0xFF3E3A4D), Color(0xFF352E42),
                    Color(0xFF3D394D), Color(0xFF352E40)
                )
            )
            else -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569),
                    Color(0xFF334155), Color(0xFF1E293B)
                )
            )
        }

        AccountType.CASHBACK, AccountType.BUSINESS -> when (colorVariant) {
            0 -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3C3B40), Color(0xFF454449), Color(0xFF3D3C41),
                    Color(0xFF444348), Color(0xFF3C3B40)
                )
            )
            1 -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF473F3F), Color(0xFF5F5252), Color(0xFF463E3E),
                    Color(0xFF5C5050), Color(0xFF463E3E)
                )
            )
            else -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1D254D), Color(0xFF283054), Color(0xFF1C254F),
                    Color(0xFF283051), Color(0xFF1E254C)
                )
            )
        }

        AccountType.DEBIT -> when (colorVariant) {
            0 -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1F2937), Color(0xFF374151), Color(0xFF4B5563),
                    Color(0xFF374151), Color(0xFF1F2937)
                )
            )
            1 -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF27272A), Color(0xFF3F3F46), Color(0xFF52525B),
                    Color(0xFF3F3F46), Color(0xFF27272A)
                )
            )
            else -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF18181B), Color(0xFF27272A), Color(0xFF3F3F46),
                    Color(0xFF27272A), Color(0xFF18181B)
                )
            )
        }
    }

    // Chip gradient
    val chipVariant = abs(nameHash) % 2
    val chipGradient = when (chipVariant) {
        0 -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFC0A062), Color(0xFFD4B571), Color(0xFFC0A062)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8965F), Color(0xFFCFB373), Color(0xFFB8965F)
            )
        )
    }

    // Chip colors
    val chipInnerColors = when (chipVariant) {
        0 -> listOf(Color(0xFFB8965F), Color(0xFFCFB373))
        else -> listOf(Color(0xFFA68B56), Color(0xFFC0A062))
    }

    val glassHighlight = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.Transparent,
            Color.White.copy(alpha = 0.1f),
            Color.Transparent,
            Color.White.copy(alpha = 0.2f)
        ),
        start = Offset(0f, 0f),
        end = Offset(800f, 800f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 16.dp else 8.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Base gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = cardGradient)
            )

            // Glass  effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = glassHighlight)
            )

            //  edge highlight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            // Card content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = when (card.accountType) {
                            AccountType.DEBIT -> "DEBIT CARD"
                            AccountType.CREDIT -> "CREDIT CARD"
                            AccountType.BUSINESS, AccountType.CASHBACK -> "CASHBACK CARD"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    //  chip
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 24.dp)
                            .background(
                                brush = chipGradient,
                                shape = RoundedCornerShape(6.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 28.dp, height = 18.dp)
                                .background(
                                    brush = Brush.linearGradient(colors = chipInnerColors),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left side - Card name and number
                    Column {
                        Text(
                            text = card.accountProductName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = card.maskedAccountNumber,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }

                    // Right side - Balance
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "BALANCE",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = card.formattedBalance,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        card.formattedCreditLimit?.let { limit ->
                            Text(
                                text = "LIMIT: $limit",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            //  shine for selected card
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = 500f
                            )
                        )
                )
            }
        }
    }
}