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
    onCardClick: (() -> Unit),
    modifier: Modifier = Modifier
) {
    // Clean, modern color schemes
    val cardColors = when (account.accountType?.lowercase()) {
        "debit" -> CardColors(
            primary = Color(0xFF1E3A8A),
            secondary = Color(0xFF3B82F6),
            text = Color.White
        )
        "credit" -> CardColors(
            primary = Color(0xFF581C87),
            secondary = Color(0xFF8B5CF6),

            text = Color.White
        )
        "cashback" -> CardColors(
            primary = Color(0xFF065F46),
            secondary = Color(0xFF10B981),
            text = Color.White
        )
        else -> CardColors(
            primary = Color(0xFF991B1B),
            secondary = Color(0xFFDC2626),
            text = Color.White
        )
    }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            cardColors.primary,
            cardColors.secondary
        )
    )

    val accountIcon = when (account.accountType?.lowercase()) {
        "debit" -> Icons.Default.CreditCard
        "credit" -> Icons.Default.Payment
        "cashback" -> Icons.Default.Redeem
        "savings" -> Icons.Default.Savings
        "business" -> Icons.Default.Business
        else -> Icons.Default.AccountBalance
    }

    val accountName = when (account.accountType?.lowercase()) {
        "debit" -> "Debit Card"
        "credit" -> "Credit Card"
        "cashback" -> "Cashback Card"
        "savings" -> "Savings Account"
        "business" -> "Business Account"
        else -> "Bank Account"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with account type and icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = accountName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = cardColors.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )

                    Icon(
                        imageVector = accountIcon,
                        contentDescription = null,
                        tint = cardColors.text.copy(alpha = 0.9f),
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Account number
                Column {
                    Text(
                        text = "Account Number",
                        style = MaterialTheme.typography.bodyMedium,
                        color = cardColors.text.copy(alpha = 0.8f),
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
                        style = MaterialTheme.typography.headlineSmall,
                        color = cardColors.text,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Balance - Main focus
                Column {
                    Text(
                        text = "Available Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = cardColors.text.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${String.format("%.3f", account.balance ?: 0.0)} KWD",
                        style = MaterialTheme.typography.displaySmall,
                        color = cardColors.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }
            }
        }
    }
}

// Simple data class for card colors
private data class CardColors(
    val primary: Color,
    val secondary: Color,
    val text: Color
)