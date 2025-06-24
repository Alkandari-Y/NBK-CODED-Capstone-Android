package com.coded.capstone.Screens.Wallet

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.composables.wallet.ApplePayCardStack
import com.coded.capstone.composables.wallet.EmptyAccountsCard
import com.coded.capstone.composables.wallet.ErrorCard
import com.coded.capstone.composables.wallet.PerksBottomSheet
import com.coded.capstone.composables.wallet.SingleSelectedCard
import com.coded.capstone.composables.wallet.TopUpDialog
import com.coded.capstone.composables.wallet.TransferDialog
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.TransactionViewModel
import com.coded.capstone.viewModels.AccountsUiState
import com.coded.capstone.data.states.TransferUiState
import com.coded.capstone.data.states.TopUpUiState
import com.coded.capstone.ui.AppBackground

@Composable
fun SuccessToast(
    message: String,
    isVisible: Boolean,
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
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF22C55E) // Green success color
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {},
    onPayAction: (AccountResponse) -> Unit = {},
    onDetailsAction: (AccountResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

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
    val accounts = (accountsUiState as? AccountsUiState.Success)?.accounts ?: emptyList()
    val perksOfAccountProduct by homeViewModel.perksOfAccountProduct.collectAsState()
    val transferUiState by transactionViewModel.transferUiState.collectAsState()
    val topUpUiState by transactionViewModel.topUpUiState.collectAsState()

    // Local States
    var selectedCard by remember { mutableStateOf<AccountResponse?>(null) }
    var currentCardIndex by remember { mutableStateOf(0) }
    var scrollVelocity by remember { mutableStateOf(0f) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    var expandedPerks by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var transferSourceAccount by remember { mutableStateOf<AccountResponse?>(null) }

    // Toast states
    var showSuccessToast by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val pagerState = rememberPagerState(pageCount = { accounts.size })

    // Handle transfer success
    LaunchedEffect(transferUiState) {
        if (transferUiState is TransferUiState.Success) {
            showTransferDialog = false
            homeViewModel.fetchAccounts() // Refresh accounts

            // Show success toast
            successMessage = "Transfer completed successfully!"
            showSuccessToast = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

            // Auto-hide toast after 3 seconds
            delay(3000)
            showSuccessToast = false

            transactionViewModel.resetTransferState()
        }
    }

    // Handle top-up success
    LaunchedEffect(topUpUiState) {
        if (topUpUiState is TopUpUiState.Success) {
            showTopUpDialog = false
            homeViewModel.fetchAccounts() // Refresh accounts

            // Show success toast
            successMessage = "Top-up completed successfully!"
            showSuccessToast = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

            // Auto-hide toast after 3 seconds
            delay(3000)
            showSuccessToast = false

            transactionViewModel.resetTopUpState()
        }
    }

    // Update current card index
    LaunchedEffect(pagerState.currentPage) {
        currentCardIndex = pagerState.currentPage
    }

    // Fetch perks when card is selected
    LaunchedEffect(selectedCard) {
        selectedCard?.let { card ->
            card.accountProductId?.let { productId ->
                homeViewModel.fetchPerksOfAccountProduct(productId.toString())
            }
        }
    }

    // Handle bottom sheet dismissal
    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet) {
            expandedPerks = false
        }
    }

    AppBackground {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Enhanced Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Wallet",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                        if (accounts.isNotEmpty() && selectedCard == null) {
                            Text(
                                text = "${currentCardIndex + 1} of ${accounts.size} accounts",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Back button when card is selected
                    AnimatedVisibility(
                        visible = selectedCard != null,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    ) {
                        IconButton(
                            onClick = {
                                selectedCard = null
                                showBottomSheet = false
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to all cards",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Handle different UI states
                when (accountsUiState) {
                    is AccountsUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF8B5CF6),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    is AccountsUiState.Error -> {
                        ErrorCard(
                            onRetry = { homeViewModel.fetchAccounts() }
                        )
                    }
                    is AccountsUiState.Success -> {
                        if (accounts.isEmpty()) {
                            EmptyAccountsCard()
                        } else {
                            // Card Display Area
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (selectedCard != null) 240.dp else 450.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (selectedCard != null) {
                                    // Single selected card at top
                                    SingleSelectedCard(
                                        account = selectedCard!!,
                                        onCardClick = { showBottomSheet = true }
                                    )
                                } else {
                                    // Apple Pay Card Stack
                                    ApplePayCardStack(
                                        accounts = accounts,
                                        selectedCard = selectedCard,
                                        pagerState = pagerState,
                                        scrollVelocity = scrollVelocity,
                                        onCardSelected = { account ->
                                            selectedCard = account
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        },
                                        onScrollVelocityChange = { velocity ->
                                            scrollVelocity = velocity
                                        }
                                    )
                                }
                            }

                            // Action Buttons (only visible when card is selected)
                            AnimatedVisibility(
                                visible = selectedCard != null,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                            ) {
                                selectedCard?.let { card ->

                                    PerksBottomSheet(
                                        account = card,
                                        perks = perksOfAccountProduct,
                                        onPayAction = {
                                            transferSourceAccount = card
                                            showTransferDialog = true
                                        },
                                        onUpgradeAccount = { /* ... */ }
                                    )
                                }
                            }

                        }
                    }
                }
            }

            // Success Toast - positioned at the top with high z-index
            SuccessToast(
                message = successMessage,
                isVisible = showSuccessToast,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp) // Position below the header
                    .zIndex(1000f) // Ensure it appears above other elements
            )

            // Transaction Dialogs
            if (showTransferDialog) {
                TransferDialog(
                    sourceAccounts = accounts,
                    defaultSource = transferSourceAccount,
                    onTransfer = { source, destination, amount ->
                        transactionViewModel.transfer(source, destination, amount)
                    },
                    onDismiss = {
                        showTransferDialog = false
                        transactionViewModel.resetTransferState()
                    },
                    transferUiState = transferUiState,
                    getEligibleDestinations = { src -> accounts.filter { it.id != src.id } },
                    validateAmount = { amount, src ->
                        if (amount > src.balance) "Insufficient balance" else null
                    }
                )
            }

//            if (showTopUpDialog) {
//                selectedCard?.let { account ->
//                    TopUpDialog(
//                        targetAccount = account,
//                        onTopUp = { amount ->
//                            transactionViewModel.topUp(amount)
//                        },
//                        onDismiss = {
//                            showTopUpDialog = false
//                            transactionViewModel.resetTopUpState()
//                        },
//                        topUpUiState = topUpUiState,
//                        validateAmount = { amount ->
//                            transactionViewModel.validateTopUpAmount(amount)
//                        }
//                    )
//                }
//            }
        }
    }
}