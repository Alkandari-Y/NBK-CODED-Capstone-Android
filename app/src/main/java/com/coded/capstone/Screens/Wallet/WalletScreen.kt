package com.coded.capstone.Screens.Wallet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coded.capstone.composables.perks.DarkEnhancedPerkItem
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.composables.wallet.WalletCard
import com.coded.capstone.composables.ui.ActionButton

//import com.coded.capstone.composables.wallet.PerksBottomSheet
import com.coded.capstone.composables.wallet.TopUpDialog
import com.coded.capstone.composables.wallet.TransferDialog
import com.coded.capstone.data.responses.perk.PerkDto
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
    var showBottomSheet by remember { mutableStateOf(false) }

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

    // Handle bottom sheet dismissal
    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet) {
            expandedPerks = false
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
//                                ActionButtonsSection(
//                                    account = card,
//                                    onPayAction = { onPayAction(card) },
//                                    onTransferAction = { showTransferDialog = true },
//                                    onTopUpAction = { showTopUpDialog = true },
//                                    onPerksAction = { showBottomSheet = true },
//                                    modifier = Modifier.padding(top = 10.dp)
//                                )
                                PerksBottomSheet(
                                    account = card,
                                    perks = perksOfAccountProduct,
                                    onPayAction={},


                                )
                            }
                        }

                    }
                }
            }
        }

//        // Enhanced Bottom Sheet for Perks - positioned to not cover the card
//        if (showBottomSheet) {
//            selectedCard?.let { card ->
//                    PerksBottomSheet(
//                        account = card,
//                        perks = perksOfAccountProduct,
//                        expanded = expandedPerks,
//                        onExpandedChange = { expandedPerks = it },
//                        onDismiss = { showBottomSheet = false },
//                    )
//                }
//
//        }

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
private fun SingleSelectedCard(
    account: AccountResponse,
    onCardClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "selectedCardScale"
    )

    val offset by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "selectedCardOffset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .offset(y = offset)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clickable(
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCardClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        WalletCard(
            account = account,
            onCardClick = onCardClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ActionButtonsSection(
    account: AccountResponse,
    onPayAction: () -> Unit,
    onTransferAction: () -> Unit,
    onTopUpAction: () -> Unit,
    onPerksAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Default.Payment,
            text = "Pay",
            onClick = onPayAction,
            backgroundColor = Color(0xFF4CAF50)
        )

        ActionButton(
            icon = Icons.Default.SwapHoriz,
            text = "Transfer",
            onClick = onTransferAction,
            backgroundColor = Color(0xFF2196F3)
        )

        ActionButton(
            icon = Icons.Default.Add,
            text = "Top Up",
            onClick = onTopUpAction,
            backgroundColor = Color(0xFFFF9800)
        )

//        ActionButton(
//            icon = Icons.Default.Star,
//            text = "Perks",
//            onClick = onPerksAction,
//            backgroundColor = Color(0xFF9C27B0)
//        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(
            indication = ripple(color = backgroundColor.copy(alpha = 0.3f)),
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    backgroundColor,
                    CircleShape
                )
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
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
            .height(450.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        accounts.forEachIndexed { index, account ->
            val baseOffset = (index * 48).dp

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .offset(y = baseOffset)
                    .graphicsLayer {
                        scaleX = 0.98f - (index * 0.02f)
                        scaleY = 0.98f - (index * 0.02f)
                        alpha = (1f - (index * 0.06f)).coerceAtLeast(0.4f)
                    }
                    .zIndex(1000f - index)
                    .shadow(
                        elevation = (20.dp - (index * 2).dp).coerceAtLeast(4.dp),
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    )
                    .clickable(
                        indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onCardSelected(account)
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                WalletCard(
                    account = account,
                    onCardClick = { onCardSelected(account) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun PerksBottomSheet(
    account: AccountResponse,
    perks: List<PerkDto>,
    onPayAction: () -> Unit,
    onUpgradeAccount: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color(0xFF404040),
                        shape = RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header with close and expand buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Account Perks",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "${perks.size} benefits available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                      ActionButton(
            icon = Icons.Default.Payment,
            text = "Pay",
            onClick = onPayAction,
            backgroundColor = Color(0xFF4CAF50)
        )
                    }


            }

            Spacer(modifier = Modifier.height(24.dp))



            // Perks Content
            if (perks.isNotEmpty()) {

                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(perks) { index, perk ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(
                                animationSpec = tween(400, delayMillis = index * 100)
                            ) + slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(400, delayMillis = index * 100)
                            )
                        ) {
                            DarkEnhancedPerkItem(perk = perk)
                        }
                    }

                    // Bottom padding for last item
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            } else {
                // No perks state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color(0xFF2D2D2D),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Perks Available Yet",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upgrade your account to unlock exclusive benefits",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onUpgradeAccount,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Explore Upgrades",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


//@Composable
//fun PerksBottomSheet(
//    account: AccountResponse,
//    perks: List<PerkDto>,
//    expanded: Boolean,
//    onExpandedChange: (Boolean) -> Unit,
//    onDismiss: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight(1f),
//        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
//        shape = RoundedCornerShape(
//            topStart = 30.dp,
//            topEnd = 30.dp,
//            bottomStart = 0.dp,
//            bottomEnd = 0.dp
//        ),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp, vertical = 20.dp)
//        ) {
//            // Header with close and expand buttons
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Account Perks",
//                    style = MaterialTheme.typography.headlineSmall.copy(
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 24.sp
//                    ),
//                    color = Color.Black
//                )
//
//                Row {
//                    IconButton(
//                        onClick = { onExpandedChange(!expanded) },
//                        modifier = Modifier
//                            .size(40.dp)
//                            .background(
//                                Color(0xFFF8F8F8),
//                                CircleShape
//                            )
//                    ) {
//                        Icon(
//                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                            contentDescription = if (expanded) "Collapse" else "Expand",
//                            tint = Color.Black,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//
//
//                }
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Perks Content
//            if (perks.isNotEmpty()) {
//                Text(
//                    text = "Your Benefits",
//                    color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//
//                LazyColumn(
//                    modifier = Modifier.fillMaxHeight(),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    itemsIndexed(perks) { index, perk ->
//                        AnimatedVisibility(
//                            visible = true,
//                            enter = fadeIn(
//                                animationSpec = tween(400, delayMillis = index * 100)
//                            ) + slideInHorizontally(
//                                initialOffsetX = { it },
//                                animationSpec = tween(400, delayMillis = index * 100)
//                            )
//                        ) {
//                            EnhancedPerkItem(perk = perk)
//                        }
//                    }
//                }
//            } else {
//                // No perks state
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .size(80.dp)
//                            .background(
//                                Color(0xFFF8F8F8),
//                                shape = CircleShape
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Star,
//                            contentDescription = null,
//                            tint = Color(0xFF757575),
//                            modifier = Modifier.size(32.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text(
//                        text = "No Perks Available Yet",
//                        color = Color.Black,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp
//                    )
//                }
//            }
//        }
//    }
//}
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