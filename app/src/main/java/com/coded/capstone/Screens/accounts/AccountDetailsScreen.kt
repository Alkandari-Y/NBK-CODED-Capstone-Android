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
import androidx.compose.ui.text.font.FontFamily
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
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.zIndex
import com.coded.capstone.R
import com.coded.capstone.data.responses.kyc.KYCResponse
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideOutHorizontally
import com.coded.capstone.composables.wallet.WalletCard
import com.coded.capstone.respositories.AccountProductRepository
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.input.pointer.pointerInput

// Roboto font family (matching WalletScreen)
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

@Composable
fun FilterDropdownMenu(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                text = { Text("All") },
                onClick = {
                    onFilterSelected("All")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Date") },
                onClick = {
                    onFilterSelected("Date")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Amount") },
                onClick = {
                    onFilterSelected("Amount")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Deposit") },
                onClick = {
                    onFilterSelected("Deposit")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Withdraw") },
                onClick = {
                    onFilterSelected("Withdraw")
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
            .padding(horizontal = 4.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF23272E) // Matching wallet bottom sheet color
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
                            fontSize = 16.sp,
                            fontFamily = RobotoFont
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTransactionDate(transaction.createdAt),
                        style = AppTypography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontFamily = RobotoFont
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
                        fontSize = 16.sp,
                        fontFamily = RobotoFont
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
                        fontSize = 12.sp,
                        fontFamily = RobotoFont
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
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    
    // Swipe animation states
    var currentCardIndex by remember { mutableStateOf(0) }
    var swipeOffset by remember { mutableStateOf(0f) }
    var isSwiping by remember { mutableStateOf(false) }
    
    // Get all accounts for swiping
    val allAccounts = when (accountsUiState) {
        is com.coded.capstone.viewModels.AccountsUiState.Success -> (accountsUiState as com.coded.capstone.viewModels.AccountsUiState.Success).accounts
        else -> emptyList()
    }
    
    // Debug logging for accounts
    LaunchedEffect(allAccounts) {
        Log.d("AccountDetailsScreen", "Available accounts: ${allAccounts.size}")
        allAccounts.forEachIndexed { index, account ->
            Log.d("AccountDetailsScreen", "Account $index: ${account.accountNumber} (ID: ${account.id})")
        }
    }
    
    // Find the initial card index based on the selected accountId
    val initialCardIndex = remember(accountId, allAccounts) {
        val index = allAccounts.indexOfFirst { it.id.toString() == accountId }.coerceAtLeast(0)
        Log.d("AccountDetailsScreen", "Initial card index: $index for accountId: $accountId")
        index
    }
    
    // Update current card index when initialCardIndex changes
    LaunchedEffect(initialCardIndex) {
        currentCardIndex = initialCardIndex
        Log.d("AccountDetailsScreen", "Set current card index to: $currentCardIndex")
    }
    
    // Get current account for display
    val currentAccount = remember(currentCardIndex, allAccounts) {
        if (allAccounts.isNotEmpty() && currentCardIndex < allAccounts.size) {
            val account = allAccounts[currentCardIndex]
            Log.d("AccountDetailsScreen", "Current account: ${account.accountNumber} at index: $currentCardIndex")
            account
        } else {
            Log.d("AccountDetailsScreen", "No current account available at index: $currentCardIndex")
            null
        }
    }

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
    
    // Fetch transactions when current account changes
    LaunchedEffect(currentAccount) {
        currentAccount?.accountNumber?.let { accountNumber ->
            Log.d("AccountDetailsScreen", "Fetching transactions for current account: $accountNumber")
            viewModel.fetchTransactionHistory(accountNumber)
        }
    }

    // Show the sheet with animation on screen load
    LaunchedEffect(Unit) {
        showSheet = true
    }

    BackHandler {
        onBack()
    }

    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFE5E7EB), // Light silver
                            Color(0xFFD1D5DB)  // Silver
                        )
                    )
                )
        ) {
            // Main content area - matching wallet layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header - matching wallet style
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Account Details",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                 color = Color(0xFF374151),
                                fontFamily = RobotoFont
                            ),
//                            color = Color.White
                        )
                        if (accountState != null) {
                            Text(
                                text = accountState?.accountType?.uppercase() ?: "ACCOUNT",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontFamily = RobotoFont,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Empty space to balance the layout
                    Spacer(modifier = Modifier.width(48.dp))
                }

                // Card Display Area - matching wallet layout
                when (val account = currentAccount) {
                    null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(450.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF8EC5FF),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(450.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            var cardVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { cardVisible = true }

                            androidx.compose.animation.AnimatedVisibility(
                                visible = cardVisible,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(durationMillis = 600)
                                ) + fadeIn(animationSpec = tween(600))
                            ) {
                                // Swipeable Card Stack
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .background(
                                            if (isSwiping) Color.Red.copy(alpha = 0.1f) else Color.Transparent
                                        )
                                        .pointerInput(allAccounts.size) {
                                            detectDragGestures(
                                                onDragStart = { 
                                                    isSwiping = true
                                                    swipeOffset = 0f
                                                    Log.d("Swipe", "Drag started")
                                                },
                                                onDragEnd = {
                                                    isSwiping = false
                                                    Log.d("Swipe", "Drag ended with offset: $swipeOffset")
                                                    // Snap to nearest card
                                                    when {
                                                        swipeOffset > 30f && currentCardIndex > 0 -> {
                                                            currentCardIndex--
                                                            Log.d("Swipe", "Swiped to previous card: $currentCardIndex")
                                                        }
                                                        swipeOffset < -30f && currentCardIndex < allAccounts.size - 1 -> {
                                                            currentCardIndex++
                                                            Log.d("Swipe", "Swiped to next card: $currentCardIndex")
                                                        }
                                                    }
                                                    swipeOffset = 0f
                                                },
                                                onDrag = { _, dragAmount ->
                                                    if (allAccounts.size > 1) {
                                                        // Prevent swiping right when at first card
                                                        if (currentCardIndex == 0 && dragAmount.x > 0) {
                                                            return@detectDragGestures
                                                        }
                                                        // Prevent swiping left when at last card
                                                        if (currentCardIndex == allAccounts.size - 1 && dragAmount.x < 0) {
                                                            return@detectDragGestures
                                                        }
                                                        swipeOffset += dragAmount.x
                                                        Log.d("Swipe", "Drag amount: ${dragAmount.x}, Total offset: $swipeOffset")
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    // Horizontal carousel for cards (like transfer screen)
                                    allAccounts.forEachIndexed { index, account ->
                                        // Calculate position relative to current index
                                        val positionOffset = index - currentCardIndex
                                        
                                        // Horizontal slide animation - cards slide in from sides
                                        val horizontalOffset by animateDpAsState(
                                            targetValue = when {
                                                positionOffset == 0 -> 0.dp // Current card at center
                                                positionOffset < 0 -> (-400 * kotlin.math.abs(positionOffset)).dp // Previous cards slide from left
                                                else -> (400 * positionOffset).dp // Next cards slide from right
                                            },
                                            animationSpec = tween(durationMillis = 400),
                                            label = "horizontalOffset_$index"
                                        )
                                        
                                        // Scale animation - only current card is full size
                                        val cardScale by animateFloatAsState(
                                            targetValue = if (positionOffset == 0) 1f else 0.85f,
                                            animationSpec = tween(durationMillis = 400),
                                            label = "cardScale_$index"
                                        )
                                        
                                        // Alpha animation - only current and adjacent cards are visible
                                        val cardAlpha by animateFloatAsState(
                                            targetValue = when (kotlin.math.abs(positionOffset)) {
                                                0 -> 1f // Current card fully visible
                                                1 -> 0.6f // Adjacent cards partially visible
                                                else -> 0f // Other cards hidden
                                            },
                                            animationSpec = tween(durationMillis = 400),
                                            label = "cardAlpha_$index"
                                        )

                                        // Show card if it's current or adjacent
                                        if (kotlin.math.abs(positionOffset) <= 1) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(220.dp)
                                                    .offset(x = horizontalOffset)
                                                    .graphicsLayer {
                                                        scaleX = cardScale
                                                        scaleY = cardScale
                                                        alpha = cardAlpha
                                                    }
                                                    .zIndex(1000f - kotlin.math.abs(positionOffset))
                                                    .shadow(
                                                        elevation = if (positionOffset == 0) 20.dp else 8.dp,
                                                        shape = RoundedCornerShape(20.dp),
                                                        ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.3f),
                                                        spotColor = Color(0xFF8EC5FF).copy(alpha = 0.5f)
                                                    ),
                                                shape = RoundedCornerShape(20.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                            ) {
                                                AccountCard(
                                                    account = account,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                    
                                    // Debug info
                                    if (isSwiping) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .background(
                                                    Color.Black.copy(alpha = 0.8f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Swipe: ${swipeOffset.toInt()}",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontFamily = RobotoFont
                                            )
                                        }
                                    }
                                    
                                    // Swipe hints - only show when cards are available
                                    if (allAccounts.size > 1) {
                                        // Left arrow hint - only show if not at the start
                                        if (currentCardIndex > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .padding(start = 4.dp)
                                                    .size(56.dp)
                                                    .zIndex(2000f)
                                                    .background(
                                                        Color.Black.copy(alpha = 0.6f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ChevronLeft,
                                                    contentDescription = "Previous card",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                        
                                        // Right arrow hint - only show if not at the end
                                        if (currentCardIndex < allAccounts.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd)
                                                    .padding(end = 4.dp)
                                                    .size(56.dp)
                                                    .zIndex(2000f)
                                                    .background(
                                                        Color.Black.copy(alpha = 0.6f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ChevronRight,
                                                    contentDescription = "Next card",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                    }
                                    
                                    // Swipe indicator dots (like transfer screen)
                                    if (allAccounts.size > 1) {
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(bottom = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            allAccounts.forEachIndexed { index, _ ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .background(
                                                            if (index == currentCardIndex) Color(0xFF8EC5FF) else Color(0xFFD1D5DB),
                                                            CircleShape
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Debug info and test buttons
                if (allAccounts.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Cards: ${currentCardIndex + 1}/${allAccounts.size}",
                            color = Color(0xFF374151),
                            fontSize = 12.sp,
                            fontFamily = RobotoFont
                        )
                        TextButton(
                            onClick = {
                                if (currentCardIndex > 0) {
                                    currentCardIndex--
                                    Log.d("AccountDetailsScreen", "Manual previous: $currentCardIndex")
                                }
                            },
                            enabled = currentCardIndex > 0
                        ) {
                            Text("← Previous", color = Color(0xFF8EC5FF))
                        }
                        TextButton(
                            onClick = {
                                if (currentCardIndex < allAccounts.size - 1) {
                                    currentCardIndex++
                                    Log.d("AccountDetailsScreen", "Manual next: $currentCardIndex")
                                }
                            },
                            enabled = currentCardIndex < allAccounts.size - 1
                        ) {
                            Text("Next →", color = Color(0xFF8EC5FF))
                        }
                    }
                }
            }

            // Bottom Sheet - matching wallet design exactly
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
                when (val account = currentAccount) {
                    null -> {}
                    else -> {
                        val dynamicSheetHeight by animateDpAsState(
                            targetValue = if (sheetExpanded) 1000.dp else 510.dp,
                            animationSpec = tween(durationMillis = 400),
                            label = "dynamicSheetHeight"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dynamicSheetHeight)
                                .clip(RoundedCornerShape(topStart = 70.dp, topEnd = 0.dp))
                                .zIndex(100f)
                                .background(Color(0xFF23272E))
                                .padding(bottom = 80.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Top row: Drag handle (center) and expand/collapse button (right) - matching wallet
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Drag handle (centered)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(48.dp)
                                                .height(5.dp)
                                                .background(
                                                    color = Color.LightGray.copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(2.5.dp)
                                                )
                                        )
                                    }
                                    // Expand/collapse button (top right)
                                    IconButton(
                                        onClick = {
                                            sheetExpanded = !sheetExpanded
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            painter = if (sheetExpanded)
                                                painterResource(id = R.drawable.baseline_keyboard_double_arrow_down_24)
                                            else
                                                painterResource(id = R.drawable.baseline_keyboard_double_arrow_up_24),
                                            contentDescription = if (sheetExpanded) "Collapse" else "Expand",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                // Sheet content
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
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccountCard(
    account: AccountResponse,
    modifier: Modifier = Modifier
) {
    // Function to get recommendation type from account product category names
    fun getRecommendationType(account: AccountResponse): String? {
        // Get the account product to match the recommendation screen logic
        val accountProduct = AccountProductRepository.accountProducts.find {
            it.id == account.accountProductId
        }

        // Use the same logic as the recommendation screen
        return when {
            accountProduct?.name?.lowercase()?.contains("travel") == true -> "travel"
            accountProduct?.name?.lowercase()?.contains("family") == true -> "family essentials"
            accountProduct?.name?.lowercase()?.contains("entertainment") == true -> "entertainment"
            accountProduct?.name?.lowercase()?.contains("shopping") == true -> "shopping"
            accountProduct?.name?.lowercase()?.contains("dining") == true -> "dining"
            accountProduct?.name?.lowercase()?.contains("health") == true -> "health"
            accountProduct?.name?.lowercase()?.contains("education") == true -> "education"
            account.accountType?.lowercase() == "credit" -> "shopping"
            account.accountType?.lowercase() == "savings" -> "family essentials"
            account.accountType?.lowercase() == "debit" -> "travel"
            else -> null // Use default account type colors instead of defaulting to shopping
        }
    }

    // Get recommendation type for this account
    val recommendationType = getRecommendationType(account)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp), clip = false)
    ) {
            WalletCard(
                account = account,
            onCardClick = { /* No flip functionality */ },
                modifier = Modifier.fillMaxSize(),
                recommendationType = recommendationType
        )
    }
}

@Composable
fun QuickActions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        QuickActionItem(icon = Icons.Default.SwapHoriz, text = "Transfer")
        QuickActionItem(icon = Icons.Default.Payment, text = "Pay")
        QuickActionItem(icon = Icons.Default.Receipt, text = "Statement")
        QuickActionItem(icon = Icons.Default.Settings, text = "Manage")
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* Handle action */ }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = AppTypography.bodyMedium.copy(fontFamily = RobotoFont),
            color = Color.White.copy(alpha = 0.9f)
        )
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
    var selectedFilter by remember { mutableStateOf("All") }
    val tabTitles = listOf("Transaction History", "Details")

    // Filter and sort transactions based on selected filter
    val filteredAndSortedTransactions = remember(transactions, selectedFilter, account?.accountNumber) {
        val currentAccountNumber = account?.accountNumber ?: ""

        val filtered = when (selectedFilter) {
            "Deposit" -> transactions.filter {
                it.destinationAccountNumber == currentAccountNumber
            }
            "Withdraw" -> transactions.filter {
                it.destinationAccountNumber != currentAccountNumber
            }
            else -> transactions // "All", "Date", "Amount" show all transactions
        }

        val sorted = when (selectedFilter) {
            "Date" -> filtered.sortedByDescending { it.createdAt }
            "Amount" -> filtered.sortedByDescending { it.amount.abs() }
            else -> filtered.sortedByDescending { it.createdAt } // Default sort by date
        }

        sorted
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Row for toggling pages - matching wallet style
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions: List<TabPosition> ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF8EC5FF) // Matching wallet accent color
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
                            style = AppTypography.bodyLarge.copy(fontFamily = RobotoFont),
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Show current filter
                    Text(
                        text = "Filter: $selectedFilter",
                        style = AppTypography.bodyMedium.copy(fontFamily = RobotoFont),
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    FilterDropdownMenu(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            selectedFilter = filter
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                if (filteredAndSortedTransactions.isEmpty()) {
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
                                text = if (selectedFilter == "All") "No transactions yet" else "No ${selectedFilter.lowercase()} transactions",
                                style = AppTypography.bodyLarge.copy(fontFamily = RobotoFont),
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
                        items(filteredAndSortedTransactions, key = { it.transactionId }) { transaction ->
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
                // Details page - similar to recommendations screen
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Get account product details
                    val accountProduct = AccountProductRepository.accountProducts.find {
                        it.id == account?.accountProductId
                    }

                    // Main title
                    item {
                        Text(
                            text = "Account Details",
                            style = AppTypography.headlineMedium.copy(fontFamily = RobotoFont),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151), // Dark gray
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Account product name header
                    item {
                        Text(
                            text = accountProduct?.name?.uppercase() ?: "ACCOUNT DETAILS",
                            style = AppTypography.headlineSmall.copy(fontFamily = RobotoFont),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Account type subtitle
                        Text(
                            text = account?.accountType?.uppercase() ?: "ACCOUNT",
                            color = Color(0xFF8EC5FF),
                            fontSize = 14.sp,
                            fontFamily = RobotoFont,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Account Information Section (at the top)
                    item {
                        Text(
                            text = "Account Information",
                            style = AppTypography.titleMedium.copy(fontFamily = RobotoFont),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Account details in the same style
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Account Number",
                                    color = Color(0xFF8EC5FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    fontFamily = RobotoFont
                                )
                                Text(
                                    account?.accountNumber ?: "-",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    fontFamily = RobotoFont
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Current Balance",
                                    color = Color(0xFF8EC5FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    fontFamily = RobotoFont
                                )
                                Text(
                                    "${String.format("%,.3f", account?.balance ?: 0.0)} KWD",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    fontFamily = RobotoFont
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Account Type",
                                    color = Color(0xFF8EC5FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    fontFamily = RobotoFont
                                )
                                Text(
                                    account?.accountType?.uppercase() ?: "-",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    fontFamily = RobotoFont
                                )
                            }
                        }
                    }

                    // Perks/info as premium InfoCards - matching recommendations screen style
                    item {
                        Text(
                            text = "Product Features",
                            style = AppTypography.titleMedium.copy(fontFamily = RobotoFont),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Interest Rate
                            accountProduct?.interestRate?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Interest Rate",
                                        color = Color(0xFF8EC5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                    Text(
                                        "${it}%",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                }
                            }

                            // Credit Limit
                            accountProduct?.creditLimit?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Credit Limit",
                                        color = Color(0xFF8EC5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                    Text(
                                        "KD ${it.toInt()}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                }
                            }

                            // Annual Fee
                            accountProduct?.annualFee?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Annual Fee",
                                        color = Color(0xFF8EC5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                    Text(
                                        if (it == 0.0) "Free" else "KD ${it.toInt()}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                }
                            }

                            // Min Balance Required
                            accountProduct?.minBalanceRequired?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Min Balance",
                                        color = Color(0xFF8EC5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                    Text(
                                        "KD ${it.toInt()}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                }
                            }

                            // Min Salary
                            accountProduct?.minSalary?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Min Salary",
                                        color = Color(0xFF8EC5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                    Text(
                                        "KD ${it.toInt()}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        fontFamily = RobotoFont
                                    )
                                }
                            }
                        }
                    }

                    // Description if available
                    accountProduct?.description?.let { description ->
                        item {
                            Text(
                                text = "Description",
                                style = AppTypography.titleMedium.copy(fontFamily = RobotoFont),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Text(
                                text = description,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontFamily = RobotoFont,
                                lineHeight = 20.sp
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

