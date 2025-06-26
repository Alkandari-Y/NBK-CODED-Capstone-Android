package com.coded.capstone.screens.accounts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.transaction.TransactionDetails
import com.coded.capstone.viewModels.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.math.BigDecimal
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.ui.draw.shadow
import com.coded.capstone.ui.AppBackground
import com.coded.capstone.ui.theme.AppTypography
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.ui.draw.blur
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.geometry.Offset
import com.coded.capstone.SVG.BaselineRemoveRedEyeIcon
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.slideInHorizontally
import com.coded.capstone.SVG.BankFillIcon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.coded.capstone.R
import com.coded.capstone.data.responses.kyc.KYCResponse

// Dark theme colors
private val DarkBackground = Color(0xFF0A0A0A)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkSurfaceVariant = Color(0xFF2A2A2A)
private val DarkOnSurface = Color(0xFFE8E8E8)
private val DarkOnSurfaceVariant = Color(0xFFB8B8B8)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentGreen = Color(0xFF10B981)
private val AccentRed = Color(0xFFEF4444)

@Composable
fun FilterDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Filter") }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.White
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Date") },
                onClick = {
                    selectedFilter = "Date"
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Amount") },
                onClick = {
                    selectedFilter = "Amount"
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Deposit") },
                onClick = {
                    selectedFilter = "Deposit"
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Withdraw") },
                onClick = {
                    selectedFilter = "Withdraw"
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: TransactionDetails,
    currentAccountNumber: String
) {
    val isReceived = transaction.destinationAccountNumber == currentAccountNumber
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction icon and details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon with background
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = if (isReceived) {
                                Color(0xFF4CAF50).copy(alpha = 0.15f)
                            } else {
                                Color(0xFFE57373).copy(alpha = 0.15f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isReceived) {
                            Icons.Default.ArrowDownward
                        } else {
                            Icons.Default.ArrowUpward
                        },
                        contentDescription = null,
                        tint = if (isReceived) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFE57373)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Transaction details
                Column {
                    Text(
                        text = transaction.category.ifEmpty { "Transaction" },
                        style = AppTypography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTransactionDate(transaction.createdAt),
                        style = AppTypography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = Color.Gray
                    )
                }
            }
            
            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (isReceived) "+" else "-"}${String.format("%,.3f", transaction.amount.abs())} KWD",
                    style = AppTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = if (isReceived) {
                        Color(0xFF4CAF50)
                    } else {
                        Color(0xFFE57373)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isReceived) "Received" else "Sent",
                    style = AppTypography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = Color.Gray
                )
            }
        }
    }
}

fun formatTransactionDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Unknown date"
    }
}

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AccountDetailsScreen(
    accountId: String,
    viewModel: HomeScreenViewModel,
    onBack: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var sheetExpanded by remember { mutableStateOf(false) }

    val sheetHeight by animateFloatAsState(
        targetValue = if (sheetExpanded) 1f else 0.6f,
        animationSpec = tween(durationMillis = 500),
        label = "sheetHeight"
    )

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
            // Wait for account details to be loaded
            var attempts = 0
            while (accountState == null && attempts < 10) {
                delay(100)
                attempts++
            }
            // Once we have the account details, fetch transactions
            accountState?.accountNumber?.let { accountNumber ->
                Log.d("AccountDetailsScreen", "Fetching transactions for account: $accountNumber")
                viewModel.fetchTransactionHistory(accountNumber)
            }
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load account details."
            isLoading = false
            Log.e("AccountDetailsScreen", "Error fetching account details: ${e.message}")
        }
    }

    // Show the sheet with animation on screen load
    LaunchedEffect(Unit) {
        showSheet = true
    }

    AppBackground {
        Box(Modifier.fillMaxSize()) {
            // TopAppBar with back button
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Main content (card)
            var cardVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { cardVisible = true }
            when (val account = accountState) {
                null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8EC5FF),
                            strokeWidth = 3.dp
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(90.dp)) // More top space
                        androidx.compose.animation.AnimatedVisibility(
                            visible = cardVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(durationMillis = 600)
                            ) + fadeIn(animationSpec = tween(600))
                        ) {
                            FlippableAccountCard(account = account)
                        }
                    }
                }
            }

            // Custom animated bottom sheet overlays the card, never pushes it
            AnimatedVisibility(
                visible = showSheet,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                when (val account = accountState) {
                    null -> {}
                    else -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(sheetHeight)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 70.dp, topEnd = 0.dp))
                            .background(Color(0xFF23272E))
                    ) {
                        TransactionHistorySheetContent(
                            transactions = transactions,
                            account = account,
                            kyc = null,
                            sheetExpanded = sheetExpanded,
                            onToggleExpand = { sheetExpanded = !sheetExpanded }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FlippableAccountCard(
    account: AccountResponse,
    modifier: Modifier = Modifier
) {
    var flipped by remember { mutableStateOf(false) }
    var showSensitive by remember { mutableStateOf(false) }

    LaunchedEffect(showSensitive) {
        if (showSensitive) {
            delay(3000)
            showSensitive = false
        }
    }

    val formattedAccountNumber = remember(account.accountNumber, showSensitive) {
        if (showSensitive) {
            account.accountNumber?.chunked(4)?.joinToString(" ") ?: ""
        } else {
            "**** **** **** ${account.accountNumber?.takeLast(4)}"
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(16.dp, RoundedCornerShape(10.dp), clip = false)
            .graphicsLayer {
                rotationY = if (flipped) 180f else 0f
                cameraDistance = 8 * density
            }
            .clickable(
                onClick = { flipped = !flipped },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        val cardModifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF7C8D94), // Gray
                        Color(0xFF181A1F)  // Dark Gray
                    )
                )
            )

        if (!flipped) {
            FrontSide(
                modifier = cardModifier,
                account = account,
                formattedAccountNumber = formattedAccountNumber,
                showSensitive = showSensitive,
                onToggle = { showSensitive = !showSensitive }
            )
        } else {
            BackSide(modifier = cardModifier)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FrontSide(
    modifier: Modifier = Modifier,
    account: AccountResponse,
    formattedAccountNumber: String,
    showSensitive: Boolean,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showCopied by remember { mutableStateOf(false) }
    Box(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "National Bank of Kuwait",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    style = AppTypography.bodySmall
                )
                BankFillIcon(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF8EC5FF)
                )
            }

            Column {
                Text(
                    text = "Balance",
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%,.3f", account.balance ?: 0.0)} KWD",
                    style = AppTypography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContent(
                    targetState = formattedAccountNumber,
                    transitionSpec = { fadeIn() with fadeOut() },
                    label = "accountNumber"
                ) { number ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = number,
                            fontSize = 16.sp,
                            color = if (showSensitive) Color(0xFF8EC5FF) else Color.White,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Normal,
                            style = AppTypography.bodyLarge
                        )
                        if (showSensitive && !account.accountNumber.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(account.accountNumber))
                                    showCopied = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = com.coded.capstone.R.drawable.baseline_file_copy_24),
                                    contentDescription = "Copy Account Number",
                                    tint = Color(0xFF8EC5FF),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
                if (showCopied) {
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(1200)
                        showCopied = false
                    }
                    Text(
                        text = "Copied!",
                        color = Color(0xFF8EC5FF),
                        style = AppTypography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = account.accountType?.uppercase() ?: "ACCOUNT",
                    style = AppTypography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                BaselineRemoveRedEyeIcon(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggle
                        ),
                    color = if (showSensitive) Color(0xFF8EC5FF) else Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun BackSide(modifier: Modifier = Modifier) {
    Box(
        modifier
            .graphicsLayer { rotationY = 180f }
    ) {
        Column {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun TransactionHistorySheetContent(
    transactions: List<TransactionDetails>,
    account: AccountResponse?,
    kyc: KYCResponse?,
    sheetExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Transaction History", "Details")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF23272E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sheet header
            // Expand/collapse button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onToggleExpand) {
                    Icon(
                        painter = painterResource(id = if (sheetExpanded) R.drawable.baseline_keyboard_double_arrow_down_24 else R.drawable.baseline_keyboard_double_arrow_up_24),
                        contentDescription = if (sheetExpanded) "Collapse" else "Expand",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row for toggling pages
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions: List<TabPosition> ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF8EC5FF)
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = AppTypography.bodyLarge,
                                color = if (selectedTab == index) Color(0xFF8EC5FF) else Color.White
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> {
                    // Transaction History
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilterDropdownMenu()
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "No transactions",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No transactions yet",
                                    style = AppTypography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(transactions, key = { it.transactionId }) { transaction ->
                                TransactionListItem(
                                    transaction = transaction,
                                    currentAccountNumber = account?.accountNumber ?: ""
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                1 -> {
                    // Details page
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (kyc != null) {
                            Text(
                                text = "Name: ${kyc.firstName} ${kyc.lastName}",
                                style = AppTypography.bodyLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Text(
                            text = "Account Details",
                            style = AppTypography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (account != null) {
                            Text(
                                "Account Type: ${account.accountType ?: "-"}",
                                style = AppTypography.bodyLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Account Number: ${account.accountNumber ?: "-"}",
                                style = AppTypography.bodyLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Balance: ${String.format("%,.3f", account.balance)} KWD",
                                style = AppTypography.bodyLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Account Product ID: ${account.accountProductId ?: "-"}",
                                style = AppTypography.bodyLarge,
                                color = Color.White
                            )
                        } else {
                            Text(
                                "No account details available.",
                                style = AppTypography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    fun getCategoryIcon(category: String?): ImageVector {
        return when (category?.lowercase()) {
            "salary" -> Icons.Default.MonetizationOn
            "freelance" -> Icons.Default.Work
            "investment" -> Icons.Default.TrendingUp
            "groceries" -> Icons.Default.ShoppingCart
            "dining" -> Icons.Default.Fastfood
            "transport" -> Icons.Default.Commute
            "utilities" -> Icons.Default.ReceiptLong
            "rent" -> Icons.Default.Home
            "health" -> Icons.Default.LocalHospital
            "entertainment" -> Icons.Default.Theaters
            "shopping" -> Icons.Default.ShoppingBag
            "travel" -> Icons.Default.Flight
            "transfer" -> Icons.Default.SwapHoriz
            else -> Icons.Default.Receipt
        }
    }
}
data class AccountColors(
    val primary: Color,
    val secondary: Color
)
