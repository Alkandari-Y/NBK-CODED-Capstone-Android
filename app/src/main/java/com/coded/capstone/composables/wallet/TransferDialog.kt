package com.coded.capstone.composables.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.states.TransferUiState
import java.math.BigDecimal
import com.coded.capstone.respositories.AccountProductRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDialog(
    sourceAccounts: List<AccountResponse>,
    defaultSource: AccountResponse? = null,
    onTransfer: (source: AccountResponse, destination: AccountResponse, amount: BigDecimal) -> Unit,
    onDismiss: () -> Unit,
    transferUiState: TransferUiState,
    getEligibleDestinations: (AccountResponse) -> List<AccountResponse>,
    validateAmount: (BigDecimal, AccountResponse) -> String?
) {
    var selectedSource by remember { mutableStateOf(defaultSource) }
    var selectedDestination by remember { mutableStateOf<AccountResponse?>(null) }
    var amount by remember { mutableStateOf("") }
    var sourceExpanded by remember { mutableStateOf(false) }
    var destinationExpanded by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf<String?>(null) }

    val destinationAccounts = selectedSource?.let { getEligibleDestinations(it) } ?: emptyList()

    // Reset destination when source changes
    LaunchedEffect(selectedSource) {
        selectedDestination = null
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transfer Funds",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // From Account Dropdown
                Text(
                    text = "FROM",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = !sourceExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSource?.let { src ->
                            val last4 = src.accountNumber?.takeLast(4) ?: ""
                            val type = src.accountType?.replaceFirstChar { it.uppercase() } ?: "Account"
                            "$type ••••$last4"
                        } ?: "Select account",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false },
                        modifier = Modifier.background(Color(0xFF2A2A2D)) // Dark background for dropdown
                    ) {
                        sourceAccounts.forEach { account ->
                            val isSelected = selectedSource?.id == account.id
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val icon = when (account.accountType?.lowercase()) {
                                            "credit" -> Icons.Default.CreditCard
                                            "savings" -> Icons.Default.Savings
                                            "debit" -> Icons.Default.Money
                                            else -> Icons.Default.AccountBalance
                                        }
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.8f)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            val product = AccountProductRepository.accountProducts.find { it.id == account.accountProductId }
                                            Text(
                                                text = "${account.accountType?.replaceFirstChar { it.uppercase() }} ••••${account.accountNumber?.takeLast(4) ?: ""}",
                                                color = if (isSelected) Color(0xFF8B5CF6) else Color.White,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                            Text(
                                                text = "${product?.name ?: "Account"} | Balance: ${account.balance} KWD",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedSource = account
                                    sourceExpanded = false
                                },
                                modifier = Modifier.background(
                                    if (isSelected) Color(0xFF8B5CF6).copy(alpha = 0.1f) else Color.Transparent
                                ),
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.White,
                                    leadingIconColor = Color.White.copy(alpha = 0.8f),
                                    trailingIconColor = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // To Account Dropdown
                Text(
                    text = "TO",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = destinationExpanded,
                    onExpandedChange = {
                        if (selectedSource != null) destinationExpanded = !destinationExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedDestination?.let { dst ->
                            val last4 = dst.accountNumber?.takeLast(4) ?: ""
                            val type = dst.accountType?.replaceFirstChar { it.uppercase() } ?: "Account"
                            "$type ••••$last4"
                        } ?: "Select destination",
                        onValueChange = { },
                        readOnly = true,
                        enabled = selectedSource != null && destinationAccounts.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destinationExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            disabledBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = destinationExpanded,
                        onDismissRequest = { destinationExpanded = false },
                        modifier = Modifier.background(Color(0xFF2A2A2D)) // Dark background for dropdown
                    ) {
                        destinationAccounts.forEach { account ->
                            val isSelected = selectedDestination?.id == account.id
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val icon = when (account.accountType?.lowercase()) {
                                            "credit" -> Icons.Default.CreditCard
                                            "savings" -> Icons.Default.Savings
                                            "debit" -> Icons.Default.Money
                                            else -> Icons.Default.AccountBalance
                                        }
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.8f)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            val product = AccountProductRepository.accountProducts.find { it.id == account.accountProductId }
                                            Text(
                                                text = "${account.accountType?.replaceFirstChar { it.uppercase() }} ••••${account.accountNumber?.takeLast(4) ?: ""}",
                                                color = if (isSelected) Color(0xFF8B5CF6) else Color.White,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                            Text(
                                                text = "${product?.name ?: "Account"} | Balance: ${account.balance} KWD",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedDestination = account
                                    destinationExpanded = false
                                },
                                modifier = Modifier.background(
                                    if (isSelected) Color(0xFF8B5CF6).copy(alpha = 0.1f) else Color.Transparent
                                ),
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.White,
                                    leadingIconColor = Color.White.copy(alpha = 0.8f),
                                    trailingIconColor = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }

                if (selectedSource != null && destinationAccounts.isEmpty()) {
                    Text(
                        text = "No eligible destination accounts",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Amount Field
                Text(
                    text = "AMOUNT",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        selectedSource?.let { source ->
                            amountError = try {
                                validateAmount(BigDecimal(amount), source)
                            } catch (e: Exception) {
                                if (amount.isNotEmpty()) "Invalid amount" else null
                            }
                        }
                    },
                    placeholder = { Text("Enter amount", color = Color.White.copy(alpha = 0.5f)) },
                    suffix = { Text("KWD", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (amountError != null) Color(0xFFEF4444) else Color(0xFF8B5CF6),
                        unfocusedBorderColor = if (amountError != null) Color(0xFFEF4444) else Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                amountError?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Global error from transfer state
                if (transferUiState is TransferUiState.Error) {
                    Text(
                        text = transferUiState.message,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Transfer Button
                Button(
                    onClick = {
                        val source = selectedSource
                        val destination = selectedDestination
                        val transferAmount = amount.toBigDecimalOrNull()

                        if (source != null && destination != null && transferAmount != null && amountError == null) {
                            onTransfer(source, destination, transferAmount)
                        }
                    },
                    enabled = selectedSource != null &&
                            selectedDestination != null &&
                            amount.isNotEmpty() &&
                            amountError == null &&
                            transferUiState != TransferUiState.Loading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6),
                        disabledContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (transferUiState == TransferUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Transfer",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}