package com.coded.capstone.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coded.capstone.wallet.data.WalletAccountDisplayModel
import java.math.BigDecimal

@Composable
fun TransactionDialog(
    title: String,
    fromLabel: String,
    accounts: List<WalletAccountDisplayModel>,
    selectedAccount: WalletAccountDisplayModel?,
    amount: BigDecimal,
    isLoading: Boolean,
    error: String?,
    onAccountSelected: (WalletAccountDisplayModel) -> Unit,
    onAmountChanged: (BigDecimal) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account Selection
                Text(
                    text = fromLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Account List
                LazyColumn(
                    modifier = Modifier.height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accounts) { account ->
                        AccountSelectionItem(
                            account = account,
                            isSelected = selectedAccount?.id == account.id,
                            onClick = { onAccountSelected(account) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input
                Text(
                    text = "Amount:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = if (amount == BigDecimal.ZERO) "" else amount.toString(),
                    onValueChange = {
                        val newAmount = try {
                            if (it.isEmpty()) BigDecimal.ZERO else BigDecimal(it)
                        } catch (e: NumberFormatException) {
                            BigDecimal.ZERO
                        }
                        onAmountChanged(newAmount)
                    },
                    placeholder = { Text("0.000", color = Color.Gray) },
                    suffix = { Text("KWD", color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Validation Messages
                error?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Insufficient funds check for transfers
                if (selectedAccount != null && amount > selectedAccount.balance && title.contains("Transfer")) {
                    Text(
                        text = "Insufficient funds",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button
                Button(
                    onClick = onConfirm,
                    enabled = !isLoading &&
                            selectedAccount != null &&
                            amount > BigDecimal.ZERO &&
                            (title.contains("Top Up") || amount <= selectedAccount.balance),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (title.contains("Top Up")) "Top Up" else "Transfer",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountSelectionItem(
    account: WalletAccountDisplayModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF2A2A2A) else Color(0xFF0F0F0F)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = account.accountProductName,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = account.maskedAccountNumber,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = account.formattedBalance,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}