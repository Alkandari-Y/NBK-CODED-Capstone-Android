package com.coded.capstone.screens.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.viewModels.HomeScreenViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Sample transaction data class
data class Transaction(
    val id: String,
    val description: String,
    val amount: Double,
    val date: Date,
    val type: TransactionType,
    val category: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountId: String,
    viewModel: HomeScreenViewModel,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // State management
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
//    var account by remember { mutableStateOf<AccountResponse?>(null) }
    val accountState by viewModel.selectedAccount.collectAsState()
    // Sample transactions (replace with actual data from backend)
    val sampleTransactions = remember {
        listOf(
            Transaction(
                id = "1",
                description = "Internet Bill",
                amount = -60.00,
                date = Date(),
                type = TransactionType.EXPENSE,
                category = "Utilities"
            ),
            Transaction(
                id = "2",
                description = "To your balance",
                amount = 30.00,
                date = Date(),
                type = TransactionType.TRANSFER
            ),
            Transaction(
                id = "3",
                description = "Masum Parvej",
                amount = 60.00,
                date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                type = TransactionType.INCOME
            ),
            Transaction(
                id = "4",
                description = "To your balance",
                amount = 70.00,
                date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                type = TransactionType.TRANSFER
            )
        )
    }

    // Fetch account details
    LaunchedEffect(accountId) {
        isLoading = true
        error = null
        try {
            viewModel.fetchAccountDetails(accountId)
            kotlinx.coroutines.delay(1000)
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load account details."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (val account = accountState) {
            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    // Account Header
                    item {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Account Icon and Type
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${account.accountType} account",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Balance
                        Row {  Text(
                            text = "${account.balance}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                            Text(
                                text = "KWD",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )}



                        Spacer(modifier = Modifier.height(24.dp))

                        // Account Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            AccountDetailRow(
                                label = "Account Number",
                                value = account.accountNumber.toString()
                            )

                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ActionButton(
                                text = "Top Up",
                                icon = Icons.Default.Add,
                                onClick = { /* Handle add */ },
                                isPrimary = true,
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Pay",
                                icon = Icons.Default.Send,
                                onClick = { /* Handle pay */ },
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Rewards",
                                icon = Icons.Default.CallReceived,
                                onClick = { /* Handle request */ },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

//                        // Search Bar
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("Search") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Today Section
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Today's Transactions
                    val todayTransactions = sampleTransactions.filter {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.date) ==
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    }

                    items(todayTransactions) { transaction ->
                        TransactionItem(transaction = transaction)
                        if (transaction != todayTransactions.last()) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Previous Date Section
                    val previousTransactions = sampleTransactions.filter {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.date) !=
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    }

                    if (previousTransactions.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "11 DEC, 2024",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        items(previousTransactions) { transaction ->
                            TransactionItem(transaction = transaction)
                            if (transaction != previousTransactions.last()) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    item {
                       Spacer(modifier = Modifier.height(24.dp))
                  }
                }
            }
        }
    }
}

@Composable
fun AccountDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(24.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Transaction Icon
        val (icon, iconColor) = when (transaction.type) {
            TransactionType.EXPENSE -> Icons.Default.ArrowUpward to MaterialTheme.colorScheme.error
            TransactionType.INCOME -> Icons.Default.ArrowDownward to MaterialTheme.colorScheme.primary
            TransactionType.TRANSFER -> Icons.Default.Add to MaterialTheme.colorScheme.primary
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Transaction Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = when (transaction.type) {
                    TransactionType.EXPENSE -> "Spent"
                    TransactionType.INCOME -> "Paid"
                    TransactionType.TRANSFER -> "Added"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Amount
        Text(
            text = if (transaction.amount >= 0) "+$${String.format("%.2f", transaction.amount)}"
            else "$${String.format("%.2f", transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (transaction.amount >= 0) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}