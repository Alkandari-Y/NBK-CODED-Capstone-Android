package com.coded.capstone.Screens.Wallet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.composables.wallet.WalletCard
import com.coded.capstone.composables.perks.EnhancedPerkItem
import com.coded.capstone.composables.ui.ActionButton
import com.coded.capstone.composables.wallet.PerksBottomSheet
import com.coded.capstone.composables.wallet.TransferDialog
import com.coded.capstone.composables.wallet.TopUpDialog
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.TransactionViewModel
import com.coded.capstone.viewModels.AccountsUiState
import com.coded.capstone.data.states.TransferUiState
import com.coded.capstone.data.states.TopUpUiState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState(pageCount = { accounts.size })

    // Handle transfer success
    LaunchedEffect(transferUiState) {
        if (transferUiState is TransferUiState.Success) {
            showTransferDialog = false
            homeViewModel.fetchAccounts() // Refresh accounts
            transactionViewModel.resetTransferState()
        }
    }

    // Handle top-up success
    LaunchedEffect(topUpUiState) {
        if (topUpUiState is TopUpUiState.Success) {
            showTopUpDialog = false
            homeViewModel.fetchAccounts() // Refresh accounts
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0B),
                        Color(0xFF1A1A1D),
                        Color(0xFF2A2A2E)
                    )
                )
            )
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
                    if (accounts.isNotEmpty()) {
                        Text(
                            text = "${currentCardIndex + 1} of ${accounts.size} accounts",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Add account button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .clickable {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Account",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
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
                        // Apple Pay Inspired Card Stack
                        ApplePayCardStack(
                            accounts = accounts,
                            selectedCard = selectedCard,
                            pagerState = pagerState,
                            scrollVelocity = scrollVelocity,
                            onCardSelected = { account ->
                                selectedCard = if (selectedCard?.id == account.id) null else account
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onScrollVelocityChange = { velocity ->
                                scrollVelocity = velocity
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Buttons (shown when card is selected)
                        AnimatedVisibility(
                            visible = selectedCard != null,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            ) + fadeIn(),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(300)
                            ) + fadeOut()
                        ) {
                            selectedCard?.let { card ->
                                ActionButtonsRow(
                                    account = card,
                                    onPayAction = onPayAction,
                                    onTopUpAction = {
                                        if (transactionViewModel.canTopUp(card)) {
                                            showTopUpDialog = true
                                        }
                                    },
                                    onTransferAction = { showTransferDialog = true },
                                    onDetailsAction = onDetailsAction,
                                    canTopUp = transactionViewModel.canTopUp(card),
                                    hapticFeedback = hapticFeedback
                                )
                            }
                        }
                    }
                }
            }
        }

        // Enhanced Bottom Sheet for Perks
        selectedCard?.let { card ->
            ModalBottomSheet(
                onDismissRequest = { selectedCard = null },
                sheetState = sheetState,
                containerColor = Color(0xFF0F0F10),
                scrimColor = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PerksBottomSheet(
                    account = card,
                    perks = perksOfAccountProduct,
                    expanded = expandedPerks,
                    onExpandedChange = { expandedPerks = it },
                    onDismiss = { selectedCard = null },
                    onPayAction = { onPayAction(card); selectedCard = null },
                    onTopUpAction = {
                        if (transactionViewModel.canTopUp(card)) {
                            showTopUpDialog = true
                            selectedCard = null
                        }
                    },
                    onTransferAction = { showTransferDialog = true; selectedCard = null },
                    onDetailsAction = { onDetailsAction(card); selectedCard = null },
                    canTopUp = transactionViewModel.canTopUp(card)
                )
            }
        }

        // Transaction Dialogs
        if (showTransferDialog) {
            TransferDialog(
                sourceAccounts = transactionViewModel.getEligibleSourceAccounts(accounts),
                onTransfer = { source, destination, amount ->
                    transactionViewModel.transfer(source, destination, amount)
                },
                onDismiss = {
                    showTransferDialog = false
                    transactionViewModel.resetTransferState()
                },
                transferUiState = transferUiState,
                getEligibleDestinations = { source ->
                    transactionViewModel.getEligibleDestinationAccounts(accounts, source)
                },
                validateAmount = { amount, sourceAccount ->
                    transactionViewModel.validateTransferAmount(amount, sourceAccount)
                }
            )
        }

        if (showTopUpDialog) {
            selectedCard?.let { account ->
                TopUpDialog(
                    targetAccount = account,
                    onTopUp = { amount ->
                        transactionViewModel.topUp(amount)
                    },
                    onDismiss = {
                        showTopUpDialog = false
                        transactionViewModel.resetTopUpState()
                    },
                    topUpUiState = topUpUiState,
                    validateAmount = { amount ->
                        transactionViewModel.validateTopUpAmount(amount)
                    }
                )
            }
        }
    }
}

@Composable
private fun ApplePayCardStack(
    accounts: List<AccountResponse>,
    selectedCard: AccountResponse?,
    pagerState: PagerState,
    scrollVelocity: Float,
    onCardSelected: (AccountResponse) -> Unit,
    onScrollVelocityChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        if (selectedCard == null) {
            // Stacked cards view
            accounts.forEachIndexed { index, account ->
                val offsetY = (index * 16).dp
                val rotation = (scrollVelocity * 0.1f).coerceIn(-15f, 15f) + (index * 2f)
                val scale = 1f - (index * 0.02f)
                val alpha = 1f - (index * 0.1f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = offsetY)
                        .graphicsLayer {
                            rotationZ = rotation
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha.coerceAtLeast(0.3f)
                        }
                        .zIndex((accounts.size - index).toFloat())
                        .pointerInput(account.id) {
                            detectDragGestures(
                                onDragEnd = {
                                    onScrollVelocityChange(0f)
                                }
                            ) { _, dragAmount ->
                                onScrollVelocityChange(dragAmount.y * 0.1f)
                            }
                        }
                ) {
                    WalletCard(
                        account = account,
                        onCardClick = { onCardSelected(account) },
                        tiltAngle = rotation,
                        scale = scale,
                        alpha = alpha.coerceAtLeast(0.3f),
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                elevation = (12 - index * 2).dp.coerceAtLeast(4.dp),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }
        } else {
            // Selected card view (enlarged and centered)
            val selectedIndex = accounts.indexOf(selectedCard)

            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(),
                modifier = Modifier.fillMaxSize()
            ) {
                WalletCard(
                    account = selectedCard,
                    onCardClick = { onCardSelected(selectedCard) },
                    scale = 1.05f,
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                )
            }

            // Fade out other cards
            accounts.forEachIndexed { index, account ->
                if (account.id != selectedCard.id) {
                    AnimatedVisibility(
                        visible = false,
                        exit = slideOutHorizontally(
                            targetOffsetX = { if (index < selectedIndex) -it else it },
                            animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(400))
                    ) {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(
    account: AccountResponse,
    onPayAction: (AccountResponse) -> Unit,
    onTopUpAction: () -> Unit,
    onTransferAction: () -> Unit,
    onDetailsAction: (AccountResponse) -> Unit,
    canTopUp: Boolean,
    hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionButton(
            icon = Icons.Default.Payment,
            label = "Pay",
            color = Color(0xFF10B981),
            onClick = {
                onPayAction(account)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = Icons.Default.Add,
            label = "Top Up",
            color = if (canTopUp) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
            onClick = {
                if (canTopUp) {
                    onTopUpAction()
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = Icons.Default.SwapHoriz,
            label = "Transfer",
            color = Color(0xFF8B5CF6),
            onClick = {
                onTransferAction()
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = Icons.Default.Info,
            label = "Details",
            color = Color(0xFF3B82F6),
            onClick = {
                onDetailsAction(account)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ErrorCard(onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F23)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading accounts",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Please try again",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

@Composable
private fun EmptyAccountsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F23)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "No accounts found",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Add your first account to get started",
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}