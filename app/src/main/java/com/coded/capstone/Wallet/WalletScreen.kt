package com.coded.capstone.Wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.Wallet.components.CardStack
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.enums.AccountType
import java.math.BigDecimal
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.SVG.TransferUsersIcon
import com.coded.capstone.SVG.CreditCardCloseIcon
import androidx.compose.material.icons.filled.Info
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {}
) {
    // Sample data - replace with actual API calls
    val sampleAccounts = remember {
        listOf(
            AccountResponse(
                accountNumber = "1234567890123456",
                id = 1L,
                balance = BigDecimal("1250.50"),
                name = "Main Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.DEBIT
            ),
            AccountResponse(
                accountNumber = "9876543210987654",
                id = 2L,
                balance = BigDecimal("500.25"),
                name = "Savings Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.CREDIT
            ),
            AccountResponse(
                accountNumber = "5555666677778888",
                id = 3L,
                balance = BigDecimal("2500.00"),
                name = "Investment Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.DEBIT
            )
        )
    }
    
    var selectedCard by remember { mutableStateOf<AccountResponse?>(null) }
    var showAccountNumber by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Wallet") },
//                actions = {
//                    IconButton(onClick = onNavigateToMap) {
//                        Icon(Icons.Default.Map, contentDescription = "Map")
//                    }
//                    IconButton(onClick = { /* Add new account */ }) {
//                        Icon(Icons.Default.Add, contentDescription = "Add Account")
//                    }
//                }
//            )
//        }
//    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Card Stack (bigger size)
            CardStack(
                accounts = sampleAccounts,
                selectedCard = selectedCard,
                onCardSelected = { selectedCard = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 0.dp)
            )
            
            // Show services row above details card when a card is selected
            if (selectedCard != null) {
                ServicesRow(
                    onTransfer = { /* handle transfer */ },
                    onTransferToOthers = { /* handle transfer to others */ },
                    onCloseAccount = { /* handle close account */ }
                )
            }
            
            // Selected Card Details
            selectedCard?.let { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Account Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow("Account Name", card.name)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Account Number",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = {
                                showAccountNumber = true
                                coroutineScope.launch {
                                    delay(3000)
                                    showAccountNumber = false
                                }
                            }) {
                                Icon(Icons.Default.Info, contentDescription = "Show Info")
                            }
                        }
                        Text(
                            text = if (showAccountNumber) card.accountNumber else formatAccountNumber(card.accountNumber),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        DetailRow("Balance", "${card.balance} KWD")
                        DetailRow("Type", card.accountType.name)
                        DetailRow("Status", if (card.active) "Active" else "Inactive")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        

                    }
                }
            }
        }
    }


@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ServiceRoundButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A1D))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        icon()
    }
}

private fun formatAccountNumber(accountNumber: String): String {
    // Mask all but last 4 digits
    return accountNumber.replaceRange(0, accountNumber.length - 4, "*".repeat(accountNumber.length - 4))
}

@Composable
fun ServicesRow(
    onTransfer: () -> Unit,
    onTransferToOthers: () -> Unit,
    onCloseAccount: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
    ) {
        ServiceRoundButton(
            icon = { CardTransferBoldIcon(modifier = Modifier.size(28.dp)) },
            onClick = onTransfer
        )
        ServiceRoundButton(
            icon = { TransferUsersIcon(modifier = Modifier.size(28.dp)) },
            onClick = onTransferToOthers
        )
        ServiceRoundButton(
            icon = { CreditCardCloseIcon(modifier = Modifier.size(28.dp)) },
            onClick = onCloseAccount
        )
    }
} 