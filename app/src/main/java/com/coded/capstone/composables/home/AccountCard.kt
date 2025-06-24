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
import com.coded.capstone.ui.theme.AppTypography

@Composable
fun AccountCard(
    account: AccountResponse,
    onCardClick: (() -> Unit),
    modifier: Modifier = Modifier
) {
    // Clean, modern color schemes
    val cardColors = when (account.accountType?.lowercase()) {
        "debit" -> CardColors(
            primary = Color(0xFF192234),
            secondary = Color(0xFF030505),
            text = Color.White
        )
        "credit" -> CardColors(
            primary = Color(0xFF1D2A31),
            secondary = Color(0xFF12121E),

            text = Color.White
        )
        "cashback" -> CardColors(
            primary = Color(0xFF636B69),
            secondary = Color(0xFF1C1B1B),
            text = Color.White
        )
        else -> CardColors(
            primary = Color(0xFF050303),
            secondary = Color(0xFF102D49),
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
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(14.dp),
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
                        style = AppTypography.headlineSmall,
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
                        style = AppTypography.bodyMedium,
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
                        style = AppTypography.headlineSmall,
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
                        style = AppTypography.bodyMedium,
                        color = cardColors.text.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${String.format("%.3f", account.balance ?: 0.0)} KWD",
                        style = AppTypography.displaySmall,
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