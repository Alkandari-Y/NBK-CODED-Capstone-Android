package com.coded.capstone.composables.home

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse

@Composable
fun AccountCard(
    account: AccountResponse,
    onCardClick: (() -> Unit)? = null
) {
    // Professional color schemes for different account types
    val cardColors = when (account.accountType?.lowercase()) {
        "debit" -> CardColors(
            primary = Color(0xFF1A1A2E),
            secondary = Color(0xFF16213E),
            accent = Color(0xFF0F3460),
            text = Color.White
        )
        "credit" -> CardColors(
            primary = Color(0xFF2C1810),
            secondary = Color(0xFF3D2914),
            accent = Color(0xFF8B4513),
            text = Color.White
        )
        "savings" -> CardColors(
            primary = Color(0xFF0D4F3C),
            secondary = Color(0xFF165A4A),
            accent = Color(0xFF2D8659),
            text = Color.White
        )
        "business" -> CardColors(
            primary = Color(0xFF1A1A1A),
            secondary = Color(0xFF2D2D2D),
            accent = Color(0xFF404040),
            text = Color.White
        )
        else -> CardColors(
            primary = Color(0xFF1A237E),
            secondary = Color(0xFF283593),
            accent = Color(0xFF3F51B5),
            text = Color.White
        )
    }

    // Subtle gradient for depth
    val backgroundGradient = Brush.linearGradient(
        listOf(
            cardColors.primary,
            cardColors.secondary,
            cardColors.accent.copy(alpha = 0.8f)
        )
    )

    // Get account icon
    val accountIcon = when (account.accountType?.lowercase()) {
        "debit" -> Icons.Default.CreditCard
        "credit" -> Icons.Default.AccountBalance
        "savings" -> Icons.Default.Savings
        "business" -> Icons.Default.Business
        else -> Icons.Default.AccountBalanceWallet
    }

    // Get account display name
    val accountName = when (account.accountType?.lowercase()) {
        "debit" -> "Current Account"
        "credit" -> "Credit Card"
        "savings" -> "Savings Account"
        "business" -> "Business Account"
        else -> "Bank Account"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clickable { onCardClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
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
                                Color.Black.copy(alpha = 0.05f)
                            ),
                            radius = 400f
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "National Bank of Kuwait",
                            style = MaterialTheme.typography.bodySmall,
                            color = cardColors.text.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = accountName,
                            style = MaterialTheme.typography.titleMedium,
                            color = cardColors.text,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    // Account type icon with minimal styling
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = accountIcon,
                                contentDescription = null,
                                tint = cardColors.text.copy(alpha = 0.9f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Account number section
                Column {
                    Text(
                        text = "Account Number",
                        style = MaterialTheme.typography.bodySmall,
                        color = cardColors.text.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (!account.accountNumber.isNullOrBlank() && account.accountNumber.length >= 4) {
                            "•••• •••• •••• ${account.accountNumber.takeLast(4)}"
                        } else {
                            "•••• •••• •••• ••••"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = cardColors.text,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                }

                // Balance section
                Column {
                    Text(
                        text = "Available Balance",
                        style = MaterialTheme.typography.bodySmall,
                        color = cardColors.text.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "KD ${String.format("%.3f", account.balance)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = cardColors.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Footer section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Account status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    Color(0xFF4CAF50),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = cardColors.text.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                    }

                    // Account type badge
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = (account.accountType?.uppercase() ?: "ACCOUNT"),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = cardColors.text,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }

            // Subtle border highlight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
        }
    }
}

// Data class for card colors
private data class CardColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val text: Color
)