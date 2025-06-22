package com.coded.capstone.screens.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.transaction.TransactionDetails
import com.coded.capstone.viewModels.HomeScreenViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.math.BigDecimal
import android.util.Log

// Dark theme colors
private val DarkBackground = Color(0xFF0A0A0A)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkSurfaceVariant = Color(0xFF2A2A2A)
private val DarkOnSurface = Color(0xFFE8E8E8)
private val DarkOnSurfaceVariant = Color(0xFFB8B8B8)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentGreen = Color(0xFF10B981)
private val AccentRed = Color(0xFFEF4444)

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
    var isAccountNumberVisible by remember { mutableStateOf(false) }
    val accountState by viewModel.selectedAccount.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Fetch account details and transactions
    LaunchedEffect(accountId) {
        Log.d("AccountDetailsScreen", "Fetching account details for ID: $accountId")
        isLoading = true
        error = null
        try {
            viewModel.fetchAccountDetails(accountId)
            kotlinx.coroutines.delay(1000)
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load account details."
            isLoading = false
            Log.e("AccountDetailsScreen", "Error fetching account details: ${e.message}")
        }
    }

    // Fetch transactions when account details are loaded
    LaunchedEffect(accountState) {
        Log.d("AccountDetailsScreen", "Account state changed: ${accountState?.accountNumber}")
        accountState?.accountNumber?.let { accountNumber ->
            Log.d("AccountDetailsScreen", "Fetching transactions for account: $accountNumber")
            viewModel.fetchTransactionHistory(accountNumber)
        }
    }

    // Debug logging for transactions
    LaunchedEffect(transactions) {
        Log.d("AccountDetailsScreen", "Transactions updated: ${transactions.size} transactions")
        transactions.forEach { transaction ->
            Log.d("AccountDetailsScreen", "Transaction: ${transaction.transactionId} - ${transaction.amount} - ${transaction.category}")
        }
    }

    // Get account colors based on type
    val accountColors = when (accountState?.accountType?.lowercase()) {
        "debit" -> AccountColors(
            primary = Color(0xFF1E3A8A),
            secondary = Color(0xFF3B82F6)
        )
        "credit" -> AccountColors(
            primary = Color(0xFF581C87),
            secondary = Color(0xFF8B5CF6)

        )
        "cashback" -> AccountColors(
            primary = Color(0xFF065F46),
            secondary = Color(0xFF10B981)
        )
        "savings" -> AccountColors(
            primary = Color(0xFF7C2D12),
            secondary = Color(0xFFEA580C)
        )
        "business" -> AccountColors(
            primary = Color(0xFF374151),
            secondary = Color(0xFF6B7280)
        )
        else -> AccountColors(
            primary = Color(0xFF991B1B),
            secondary = Color(0xFFDC2626)

        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    DarkSurface,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = DarkOnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { /* More options */ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    DarkSurface,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = DarkOnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                        CircularProgressIndicator(
                            color = AccentBlue,
                            strokeWidth = 3.dp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        // Account Header with gradient background
                        item {
                            Spacer(modifier = Modifier.height(20.dp))

                            // Main account card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    accountColors.primary,
                                                    accountColors.secondary
                                                )
                                            )
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Header
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "National Bank of Kuwait",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.White.copy(alpha = 0.8f),
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${account.accountType?.replaceFirstChar {
                                                        if (it.isLowerCase()) it.titlecase() else it.toString()
                                                    }} Account",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .background(
                                                        Color.White.copy(alpha = 0.2f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    getAccountIcon(account.accountType),
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }

                                        // Account number with toggle visibility
                                        Column {
                                            Text(
                                                text = "Account Number",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = if (isAccountNumberVisible && !account.accountNumber.isNullOrBlank()) {
                                                        account.accountNumber
                                                    } else if (!account.accountNumber.isNullOrBlank() && account.accountNumber.length >= 4) {
                                                        "•••• •••• •••• ${account.accountNumber.takeLast(4)}"
                                                    } else {
                                                        "•••• •••• •••• ••••"
                                                    },
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 16.sp,
                                                    letterSpacing = 1.sp,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                IconButton(
                                                    onClick = { isAccountNumberVisible = !isAccountNumberVisible },
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(
                                                            Color.White.copy(alpha = 0.2f),
                                                            CircleShape
                                                        )
                                                ) {
                                                    Icon(
                                                        imageVector = if (isAccountNumberVisible) {
                                                            Icons.Default.VisibilityOff
                                                        } else {
                                                            Icons.Default.Visibility
                                                        },
                                                        contentDescription = if (isAccountNumberVisible) {
                                                            "Hide account number"
                                                        } else {
                                                            "Show account number"
                                                        },
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Balance
                                        Column {
                                            Text(
                                                text = "Available Balance",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${String.format("%.3f", account.balance ?: 0.0)} KWD",
                                                style = MaterialTheme.typography.displaySmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 32.sp
                                            )
                                        }
                                    }
                                }
                            }

//                            Spacer(modifier = Modifier.height(32.dp))
//
//                            // Action Buttons
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(12.dp)
//                            ) {
//                                ActionButton(
//                                    text = "Top Up",
//                                    icon = Icons.Default.Add,
//                                    onClick = { /* Handle add */ },
//                                    isPrimary = true,
//                                    modifier = Modifier.weight(1f)
//                                )
//                                ActionButton(
//                                    text = "Pay",
//                                    icon = Icons.Default.Send,
//                                    onClick = { /* Handle pay */ },
//                                    modifier = Modifier.weight(1f)
//                                )
//                                ActionButton(
//                                    text = "Perks",
//                                    icon = Icons.Default.SwapHoriz,
//                                    onClick = { /* Handle transfer */ },
//                                    modifier = Modifier.weight(1f)
//                                )
//                            }

                            Spacer(modifier = Modifier.height(32.dp))

//                            // Search Bar
//                            OutlinedTextField(
//                                value = "",
//                                onValueChange = { },
//                                placeholder = {
//                                    Text(
//                                        "Search transactions...",
//                                        color = DarkOnSurfaceVariant
//                                    )
//                                },
//                                leadingIcon = {
//                                    Icon(
//                                        Icons.Default.Search,
//                                        contentDescription = null,
//                                        tint = DarkOnSurfaceVariant
//                                    )
//                                },
//                                modifier = Modifier.fillMaxWidth(),
//                                shape = RoundedCornerShape(16.dp),
//                                colors = OutlinedTextFieldDefaults.colors(
//                                    focusedBorderColor = accountColors.secondary,
//                                    unfocusedBorderColor = DarkSurfaceVariant,
//                                    focusedContainerColor = DarkSurface,
//                                    unfocusedContainerColor = DarkSurface,
//                                    focusedTextColor = DarkOnSurface,
//                                    unfocusedTextColor = DarkOnSurface
//                                )
//                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        // Transaction History Section
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Transaction History",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DarkOnSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Debug: ${transactions.size} transactions loaded",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkOnSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }

                                TextButton(
                                    onClick = { /* View all */ }
                                ) {
                                    Text(
                                        text = "View All",
                                        color = accountColors.secondary,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Check if there are any transactions
                        if (transactions.isEmpty()) {
                            // No transactions state
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(48.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(
                                                    DarkSurfaceVariant,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Receipt,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp),
                                                tint = DarkOnSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Text(
                                            text = "No transactions yet",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = DarkOnSurface,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Your transaction history will appear here",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = DarkOnSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            // Transaction items would go here
                            items(transactions) { transaction ->
                                TransactionItem(transaction = transaction)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = Color.White
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = DarkSurface,
                contentColor = DarkOnSurface
            )
        },
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isPrimary) 4.dp else 0.dp
        )
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun TransactionItem(transaction: TransactionDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Icon
            val (icon, iconColor) = when (getTransactionType(transaction)) {
                TransactionType.EXPENSE -> Icons.Default.ArrowUpward to AccentRed
                TransactionType.INCOME -> Icons.Default.ArrowDownward to AccentGreen
                TransactionType.TRANSFER -> Icons.Default.SwapHoriz to AccentBlue
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.2f), CircleShape),
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
                    text = transaction.category.ifEmpty { "Transaction" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = DarkOnSurface
                )
                Text(
                    text = formatTransactionDate(transaction.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }

            // Amount
            Text(
                text = "${String.format("%.3f", transaction.amount)} KWD",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.amount >= BigDecimal.ZERO) AccentGreen else DarkOnSurface
            )
        }
    }
}

private fun getTransactionType(transaction: TransactionDetails): TransactionType {
    return when {
        transaction.amount < BigDecimal.ZERO -> TransactionType.EXPENSE
        transaction.amount > BigDecimal.ZERO -> TransactionType.INCOME
        else -> TransactionType.TRANSFER
    }
}

private fun formatTransactionDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Unknown date"
    }
}

private fun getAccountIcon(accountType: String?): ImageVector {
    return when (accountType?.lowercase()) {
        "debit" -> Icons.Default.CreditCard
        "credit" -> Icons.Default.Payment
        "cashback" -> Icons.Default.Redeem
        "savings" -> Icons.Default.Savings
        "business" -> Icons.Default.Business
        else -> Icons.Default.AccountBalance
    }
}

private data class AccountColors(
    val primary: Color,
    val secondary: Color
)