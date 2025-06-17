package com.coded.capstone.Wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.Wallet.components.CardStack
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.enums.AccountType
import java.math.BigDecimal

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


    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wallet") },
                actions = {
                    IconButton(onClick = onNavigateToMap) {
                        Icon(Icons.Default.Map, contentDescription = "Map")
                    }
                    IconButton(onClick = { /* Add new account */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Account")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            
            // Card Stack
            CardStack(
                accounts = sampleAccounts,
                selectedCard = selectedCard,
                onCardSelected = { selectedCard = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            )
            
            // Selected Card Details
            selectedCard?.let { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Account Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow("Account Name", card.name)
                        DetailRow("Account Number", card.accountNumber)
                        DetailRow("Balance", "${card.balance} KWD")
                        DetailRow("Type", card.accountType.name)
                        DetailRow("Status", if (card.active) "Active" else "Inactive")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Transfer money */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Transfer")
                            }
                            
                            OutlinedButton(
                                onClick = { /* View transactions */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.History, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("History")
                            }
                        }
                    }
                }
            }
            
            // Quick Actions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Send,
                            label = "Send",
                            onClick = { /* Send money */ }
                        )
                        QuickActionButton(
                            icon = Icons.Default.QrCode,
                            label = "Scan",
                            onClick = { /* Scan QR */ }
                        )
                        QuickActionButton(
                            icon = Icons.Default.Payment,
                            label = "Pay",
                            onClick = { /* Make payment */ }
                        )
                    }
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
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
} 