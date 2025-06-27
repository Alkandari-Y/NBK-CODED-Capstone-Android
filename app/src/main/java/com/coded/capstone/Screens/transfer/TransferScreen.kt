package com.coded.capstone.screens.transfer

import Transfer2FillIcon
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.coded.capstone.R
import com.coded.capstone.composables.wallet.WalletCard
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.data.states.TransferUiState
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.ui.AppBackground
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import java.math.BigDecimal
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.TransactionViewModel
import com.coded.capstone.viewModels.AccountsUiState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState

// Roboto font family
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    navController: NavController,
    selectedAccountId: String? = null,
    accounts: List<AccountResponse> = emptyList(),
    defaultSource: AccountResponse? = null,
    onTransfer: (source: AccountResponse, destination: AccountResponse, amount: BigDecimal) -> Unit = { _, _, _ -> },
    onBack: () -> Unit = { navController.navigate(NavRoutes.NAV_ROUTE_HOME) },
    transferUiState: TransferUiState = TransferUiState.Idle,
    validateAmount: (BigDecimal, AccountResponse) -> String? = { _, _ -> null }
) {
    BackHandler { onBack() }
    
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    
    // ViewModels
    val homeViewModel: HomeScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(context) as T
            }
        }
    )

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TransactionViewModel(context) as T
            }
        }
    )

    // States
    val accountsUiState by homeViewModel.accountsUiState.collectAsState()
    val allAccounts = if (accounts.isEmpty()) {
        (accountsUiState as? AccountsUiState.Success)?.accounts ?: emptyList()
    } else {
        accounts
    }
    
    // Filter accounts by type for transfer functionality
    // Source accounts: debit and credit accounts (exclude cashback)
    val sourceAccounts = allAccounts.filter { account ->
        val accountType = account.accountType?.lowercase()
        accountType == "debit" || accountType == "credit"
    }
    
    // Dynamic destination accounts based on source account type
    val getDestinationAccounts = { sourceAccount: AccountResponse? ->
        when (sourceAccount?.accountType?.lowercase()) {
            "credit" -> {
                // Credit source can only transfer to credit accounts
                allAccounts.filter { account ->
                    account.accountType?.lowercase() == "credit" && account.id != sourceAccount.id
                }.distinctBy { it.id }
            }
            "debit" -> {
                // Debit source can transfer to debit or credit accounts
                allAccounts.filter { account ->
                    val accountType = account.accountType?.lowercase()
                    (accountType == "debit" || accountType == "credit") && account.id != sourceAccount.id
                }.distinctBy { it.id }
            }
            else -> emptyList()
        }
    }
    
    val actualTransferUiState by transactionViewModel.transferUiState.collectAsState()
    
    // Fetch accounts if not provided
    LaunchedEffect(Unit) {
        if (accounts.isEmpty()) {
            homeViewModel.fetchAccounts()
        }
    }
    
    // Initialize fromCard with a valid source account (debit or credit)
    var fromCard by remember { mutableStateOf<AccountResponse?>(null) }
    
    // Set initial source card (debit or credit account)
    LaunchedEffect(sourceAccounts, defaultSource) {
        fromCard = if (defaultSource != null && sourceAccounts.contains(defaultSource)) {
            defaultSource
        } else {
            sourceAccounts.firstOrNull()
        }
    }
    
    // Success message state
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // Set default source card based on selectedAccountId (if it's a valid source account)
    LaunchedEffect(sourceAccounts, selectedAccountId) {
        if (selectedAccountId != null && sourceAccounts.isNotEmpty()) {
            val selectedAccount = sourceAccounts.find { it.id.toString() == selectedAccountId }
            if (selectedAccount != null) {
                fromCard = selectedAccount
            }
        }
    }
    
    // Handle successful transfer
    LaunchedEffect(actualTransferUiState) {
        if (actualTransferUiState is TransferUiState.Success) {
            // Show success message
            showSuccessMessage = true
            hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
        }
    }
    
    var currentToIndex by remember { mutableStateOf(0) }
    var isSwapping by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    var selectedCurrency by remember { mutableStateOf("KWD") }
    var currencyExpanded by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // Get available destination cards based on source account type
    val availableDestinations = getDestinationAccounts(fromCard)
    val toCard = availableDestinations.getOrNull(currentToIndex)
    
    // Reset destination when source changes or available destinations change
    LaunchedEffect(fromCard, availableDestinations.size) {
        if (fromCard != null && availableDestinations.isNotEmpty()) {
            // Reset to first available destination
            currentToIndex = 0
        } else if (availableDestinations.isEmpty()) {
            currentToIndex = 0
        }
    }
    
    // Ensure currentToIndex is always within bounds
    LaunchedEffect(availableDestinations, currentToIndex) {
        if (availableDestinations.isNotEmpty() && currentToIndex >= availableDestinations.size) {
            currentToIndex = 0
        }
    }
    
    // Validate amount when it changes
    LaunchedEffect(amount, fromCard) {
        if (amount.isNotEmpty() && fromCard != null) {
            try {
                val amountValue = BigDecimal(amount)
                amountError = validateAmount(amountValue, fromCard!!)
            } catch (e: Exception) {
                amountError = "Invalid amount"
            }
        } else {
            amountError = null
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF23272E).copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF23272E),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    "Transfer",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RobotoFont
                    ),
                    color = Color(0xFF23272E)
                )

                Spacer(modifier = Modifier.width(40.dp))
            }

            // Transfer restrictions information
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Credit accounts can only transfer to credit accounts. Debit accounts can transfer to debit or credit accounts.",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    fontFamily = RobotoFont,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Debug information
            when (accountsUiState) {
                is AccountsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8EC5FF),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                is AccountsUiState.Error -> {
                    Text(
                        text = "Error loading accounts: ${(accountsUiState as AccountsUiState.Error).message}",
                        color = Color(0xFFEF4444),
                        fontSize = 14.sp,
                        fontFamily = RobotoFont,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                is AccountsUiState.Success -> {
                    if (sourceAccounts.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "No valid accounts available for transfers. You need at least one debit or credit account to send transfers.",
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                fontFamily = RobotoFont,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (availableDestinations.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = when (fromCard?.accountType?.lowercase()) {
                                    "credit" -> "No credit accounts available for destination. Credit accounts can only transfer to other credit accounts."
                                    "debit" -> "No valid destination accounts available. Debit accounts can transfer to debit or credit accounts."
                                    else -> "No valid destination accounts available for transfers."
                                },
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                fontFamily = RobotoFont,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Card Selection Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.zIndex(0f)
                ) {
                    // From Card
                    AnimatedContent(
                        targetState = fromCard,
                        transitionSpec = {
                            if (isSwapping) {
                                (slideInVertically { it } + fadeIn()) with (slideOutVertically { it } + fadeOut())
                            } else {
                                (slideInHorizontally { it } + fadeIn()) with (slideOutHorizontally { -it } + fadeOut())
                            }
                        },
                        label = "FromCard"
                    ) { card ->
                        if (card != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                WalletCard(
                                    account = card,
                                    onCardClick = { /* No swiping for selected card */ },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // To Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .pointerInput(availableDestinations) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (!isSwapping && availableDestinations.isNotEmpty() && availableDestinations.size > 1) {
                                        if (dragAmount < -40) {
                                            // Swipe left - go to next destination
                                            currentToIndex = (currentToIndex + 1) % availableDestinations.size
                                            hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                        } else if (dragAmount > 40) {
                                            // Swipe right - go to previous destination
                                            currentToIndex = if (currentToIndex > 0) {
                                                currentToIndex - 1
                                            } else {
                                                availableDestinations.size - 1
                                            }
                                            hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        var shakeAnimation by remember { mutableStateOf(false) }
                        
                        // Trigger shake animation when swiping in wrong direction
                        LaunchedEffect(shakeAnimation) {
                            if (shakeAnimation) {
                                delay(300)
                                shakeAnimation = false
                            }
                        }
                        
                        AnimatedContent(
                            targetState = toCard,
                            transitionSpec = {
                                if (isSwapping) {
                                    (slideInVertically { -it } + fadeIn()) with (slideOutVertically { -it } + fadeOut())
                                } else {
                                    (slideInHorizontally { it } + fadeIn()) with (slideOutHorizontally { -it } + fadeOut())
                                }
                            },
                            label = "ToCard"
                        ) { card ->
                            if (card != null) {
                                val shakeOffset by animateDpAsState(
                                    targetValue = if (shakeAnimation) 10.dp else 0.dp,
                                    animationSpec = tween(durationMillis = 100)
                                )
                                
                                Box(
                                    modifier = Modifier.offset(x = shakeOffset)
                                ) {
                                    WalletCard(
                                        account = card,
                                        onCardClick = { /* Handled by drag */ },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // Swap Button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .zIndex(1f)
                        .background(Color(0xFF23272E), shape = RoundedCornerShape(32.dp))
                        .border(4.dp, Color.White, shape = RoundedCornerShape(32.dp))
                        .clickable {
                            if (!isSwapping && fromCard != null && toCard != null) {
                                // Check if swap is valid:
                                // 1. Destination card must be a valid source account (debit or credit)
                                // 2. Current source must be a valid destination for the swapped scenario
                                val canToCardBeSource = sourceAccounts.contains(toCard)
                                val willFromCardBeValidDestination = getDestinationAccounts(toCard).contains(fromCard)
                                
                                if (canToCardBeSource && willFromCardBeValidDestination) {
                                    coroutineScope.launch {
                                        isSwapping = true
                                        delay(300)
                                        val temp = fromCard
                                        fromCard = toCard
                                        currentToIndex = getDestinationAccounts(fromCard).indexOf(temp).coerceAtLeast(0)
                                        delay(300)
                                        isSwapping = false
                                    }
                                } else {
                                    // Show feedback that swap is not allowed due to account type restrictions
                                    hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Transfer2FillIcon(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF8EC5FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount", color = Color.DarkGray) },
                placeholder = { Text("Enter amount", color = Color.White.copy(alpha = 0.8f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFF3F4F6),
                    focusedContainerColor = Color(0xFFF3F4F6),
                    unfocusedTextColor = Color(0xFF374151),
                    focusedTextColor = Color(0xFF374151),
                    cursorColor = Color(0xFF374151)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = RobotoFont
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Error message
            amountError?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontFamily = RobotoFont,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Global error from transfer state
            if (actualTransferUiState is TransferUiState.Error) {
                Text(
                    text = (actualTransferUiState as TransferUiState.Error).message,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontFamily = RobotoFont,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(0.dp))

            // Transfer Button
            Button(
                onClick = {
                    val source = fromCard
                    val destination = toCard
                    val transferAmount = amount.toBigDecimalOrNull()

                    if (source != null && destination != null && transferAmount != null && amountError == null) {
                        transactionViewModel.transfer(source, destination, transferAmount)
                    }
                },
                enabled = fromCard != null &&
                        toCard != null &&
                        amount.isNotEmpty() &&
                        amountError == null &&
                        actualTransferUiState != TransferUiState.Loading &&
                        sourceAccounts.isNotEmpty() &&
                        availableDestinations.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8EC5FF),
                    disabledContainerColor = Color(0xFF23272E).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (actualTransferUiState == TransferUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Transfer",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = RobotoFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    
    // Success Message - positioned in the center of the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f) // Ensure it appears above other elements
    ) {
        TransferSuccessMessage(
            isVisible = showSuccessMessage,
            onDismiss = {
                showSuccessMessage = false
                navController.navigate(NavRoutes.NAV_ROUTE_HOME)
                transactionViewModel.resetTransferState()
            },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun TransferSuccessMessage(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF8EC5FF),
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Transfer Successful!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoFont
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your money has been transferred successfully to the destination account.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontFamily = RobotoFont
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8EC5FF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = RobotoFont
                    )
                }
            }
        }
    }
}