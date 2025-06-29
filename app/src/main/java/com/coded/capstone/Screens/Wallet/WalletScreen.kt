package com.coded.capstone.Screens.Wallet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coded.capstone.R
import kotlinx.coroutines.delay
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.composables.wallet.ApplePayCardStack
import com.coded.capstone.composables.wallet.EmptyAccountsCard
import com.coded.capstone.composables.wallet.ErrorCard
import com.coded.capstone.composables.wallet.PerksBottomSheet
import com.coded.capstone.composables.wallet.SingleSelectedCard
import com.coded.capstone.composables.wallet.TopUpDialog
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.TransactionViewModel
import com.coded.capstone.viewModels.AccountsUiState
import com.coded.capstone.data.states.TransferUiState
import com.coded.capstone.data.states.TopUpUiState
import com.coded.capstone.ui.AppBackground
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.SVG.RoundTapAndPlayIcon
import java.math.BigDecimal
import androidx.navigation.NavController
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.data.enums.AccountType
import com.coded.capstone.respositories.UserRepository
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import com.coded.capstone.ui.theme.AppTypography
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource

// Roboto font family
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

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
    navController: NavController,
    preSelectedAccountId: String? = null,
    onNavigateToMap: () -> Unit = {},
    onPayAction: (AccountResponse) -> Unit = {},
    onDetailsAction: (AccountResponse) -> Unit = {},
    onNavigateToTransfer: () -> Unit = {}
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
    var isFirstLoad by remember { mutableStateOf(true) }
    var cardAnimationTrigger by remember { mutableStateOf(false) }
    var externalExpandTrigger by remember { mutableStateOf(false) }

    // Pay animation states
    var isPayAnimationActive by remember { mutableStateOf(false) }
    var payAnimationSeconds by remember { mutableStateOf(59) }
    var waveAnimationKey by remember { mutableStateOf(0) }
    val payAnimationRotation by animateFloatAsState(
        targetValue = if (isPayAnimationActive) 90f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
        label = "payAnimationRotation"
    )
    val payAnimationScale by animateFloatAsState(
        targetValue = if (isPayAnimationActive) 1.2f else 1f,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
        label = "payAnimationScale"
    )
    val payAnimationOffset by animateDpAsState(
        targetValue = if (isPayAnimationActive) 200.dp else 0.dp,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
        label = "payAnimationOffset"
    )
    val buttonsOpacity by animateFloatAsState(
        targetValue = if (isPayAnimationActive) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "buttonsOpacity"
    )
    val sheetOpacity by animateFloatAsState(
        targetValue = if (isPayAnimationActive) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "sheetOpacity"
    )

    // Toast states
    var showSuccessToast by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val pagerState = rememberPagerState(pageCount = { accounts.size })

    // Auto-trigger bottom sheet on first load when accounts are available
    LaunchedEffect(accounts, isFirstLoad) {
        if (isFirstLoad && accounts.isNotEmpty()) {
            // Don't auto-select a card, let users see the card stack first
            isFirstLoad = false
        }
    }

    // Handle pre-selected account from navigation
    LaunchedEffect(preSelectedAccountId, accounts) {
        if (!preSelectedAccountId.isNullOrBlank() && accounts.isNotEmpty()) {
            val accountToSelect = accounts.find { it.id.toString() == preSelectedAccountId }
            if (accountToSelect != null) {
                selectedCard = accountToSelect
                showBottomSheet = true
                isFirstLoad = false
            }
        }
    }

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

    // Handle pay animation with counter
    LaunchedEffect(isPayAnimationActive) {
        if (isPayAnimationActive) {
            // Countdown from 59 to 0
            for (i in 59 downTo 0) {
                payAnimationSeconds = i
                waveAnimationKey++ // Trigger wave animation restart
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(1000) // Wait 1 second
            }
            // Reset animation after completion
            isPayAnimationActive = false
            waveAnimationKey = 0
            payAnimationSeconds = 59
            showBottomSheet = true // Show bottom sheet again
        }
    }

    // BackHandler for sheet
    BackHandler(enabled = showBottomSheet) {
        if (expandedPerks) {
            expandedPerks = false // Collapse the sheet to its original height
        } else if (showBottomSheet) {
            showBottomSheet = false
            expandedPerks = false
            // Do NOT set selectedCard = null here!
            // Let the UI handle hiding the sheet first, then clear selectedCard in a LaunchedEffect
        }
    }

    // Clear selectedCard only after the sheet is fully hidden
    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet && !isPayAnimationActive) {
            selectedCard = null
            cardAnimationTrigger = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .clickable(
                        enabled = selectedCard == null // Only enable when no card is selected
                    ) {
                        // Trigger external expansion
                        externalExpandTrigger = !externalExpandTrigger
                    }
            ) {
                // Enhanced Header with back button on the left
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (selectedCard != null) 10.dp else 10.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button disabled

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (selectedCard != null) "Card Details" else "My Wallet",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = if (selectedCard != null) 20.sp else 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = RobotoFont
                            ),
                            color = Color.White
                        )
                        if (accounts.isNotEmpty() && selectedCard == null) {
                            Text(
                                text = "${currentCardIndex + 1} of ${accounts.size} accounts",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontFamily = RobotoFont,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Empty space to balance the layout when back button is visible
                    if (selectedCard != null) {
                        Spacer(modifier = Modifier.width(20.dp))
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
                                color = Color(0xFF55B1EF),
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
                                    .height(if (selectedCard != null) 320.dp else 450.dp)
                                    .background(Color.White),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (selectedCard != null) {
                                    // Single selected card at top - show the actual selected card
                                    val cardOffset by animateDpAsState(
                                        targetValue = if (cardAnimationTrigger) 0.dp else (-100).dp,
                                        animationSpec = tween(durationMillis = 600),
                                        label = "cardOffset"
                                    )

                                    // Transfer button slide-in animation
                                    val transferButtonOffset by animateDpAsState(
                                        targetValue = if (cardAnimationTrigger) 0.dp else (-100).dp,
                                        animationSpec = tween(durationMillis = 600, delayMillis = 200),
                                        label = "transferButtonOffset"
                                    )

                                    // Pay button slide-in animation
                                    val payButtonOffset by animateDpAsState(
                                        targetValue = if (cardAnimationTrigger) 0.dp else 100.dp,
                                        animationSpec = tween(durationMillis = 600, delayMillis = 200),
                                        label = "payButtonOffset"
                                    )

                                    // Trigger animation when card is selected
                                    LaunchedEffect(selectedCard) {
                                        cardAnimationTrigger = true
                                    }

                                    // Vertical layout: Card -> Transfer Button
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Card with pay animation
                                        Box(
                                            modifier = Modifier
                                                .offset(y = cardOffset + payAnimationOffset)
                                                .graphicsLayer(
                                                    rotationZ = payAnimationRotation,
                                                    scaleX = payAnimationScale,
                                                    scaleY = payAnimationScale
                                                )
                                        ) {
                                            SingleSelectedCard(
                                                account = selectedCard!!,
                                                onCardClick = {
                                                    if (!isPayAnimationActive) {
                                                        // Go back to card stack instead of showing bottom sheet
                                                        showBottomSheet = false
                                                        selectedCard = null
                                                        cardAnimationTrigger = false
                                                    }
                                                }
                                            )
                                        }

                                        // Buttons row
                                        Spacer(modifier = Modifier.height(13.dp))

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .graphicsLayer(alpha = buttonsOpacity),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(modifier = Modifier.width(20.dp))

                                            // Transfer button positioned to the left - only show if not credit card
                                            if (selectedCard?.accountType?.lowercase() != "credit") {
                                                Box(
                                                    modifier = Modifier.offset(x = transferButtonOffset)
                                                ) {
                                                    // Circular shadow
                                                    Box(
                                                        modifier = Modifier
                                                            .size(45.dp)
                                                            .background(
                                                                Color.Black.copy(alpha = 0.2f),
                                                                CircleShape
                                                            )
                                                            .offset(y = 4.dp)
                                                    )

                                                    Box(
                                                        modifier = Modifier
                                                            .size(45.dp)
                                                            .background(
                                                                Color(0xFF8EC5FF).copy(alpha = 0.99f),
                                                                CircleShape
                                                            )
                                                            .clickable {
                                                                if (!isPayAnimationActive) {
                                                                    transferSourceAccount = selectedCard!!
                                                                    navController.navigate("${NavRoutes.NAV_ROUTE_TRANSFER}?selectedAccountId=${selectedCard!!.id}")
                                                                }
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CardTransferBoldIcon(
                                                            modifier = Modifier.size(32.dp),
                                                            color = Color.White
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(16.dp))
                                            }

                                            // Pay button positioned next to transfer
                                            Box(
                                                modifier = Modifier.offset(x = payButtonOffset)
                                            ) {
                                                // Circular shadow
                                                Box(
                                                    modifier = Modifier
                                                        .size(45.dp)
                                                        .background(
                                                            Color.Black.copy(alpha = 0.2f),
                                                            CircleShape
                                                        )
                                                        .offset(y = 4.dp)
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .size(45.dp)
                                                        .background(
                                                            Color(0xFF8EC5FF).copy(alpha = 0.99f),
                                                            CircleShape
                                                        )
                                                        .clickable {
                                                            if (!isPayAnimationActive) {
                                                                isPayAnimationActive = true
                                                                showBottomSheet = false
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    RoundTapAndPlayIcon(
                                                        modifier = Modifier.size(32.dp),
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Apple Pay Card Stack
                                    ApplePayCardStack(
                                        accounts = accounts.sortedBy { account ->
                                            // Get account product name to check if it's cashback
                                            val accountProduct = com.coded.capstone.respositories.AccountProductRepository.accountProducts.find {
                                                it.id == account.accountProductId
                                            }
                                            val productName = accountProduct?.name?.lowercase() ?: ""
                                            
                                            // Cashback cards first (false sorts before true)
                                            !productName.contains("cashback")
                                        },
                                        selectedCard = selectedCard,
                                        pagerState = pagerState,
                                        scrollVelocity = scrollVelocity,
                                        onCardSelected = { account ->
                                            selectedCard = account
                                            showBottomSheet = true // Auto-expand bottom sheet
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        },
                                        onScrollVelocityChange = { velocity ->
                                            scrollVelocity = velocity
                                        },
                                        externalExpandTrigger = externalExpandTrigger
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Sheet - positioned outside the Column to take full width
            selectedCard?.let { card ->
                var sheetExpanded by remember { mutableStateOf(false) }

                val sheetHeight by animateDpAsState(
                    targetValue = if (showBottomSheet) 0.dp else 1000.dp,
                    animationSpec = tween(durationMillis = 600),
                    label = "sheetHeight"
                )

                val dynamicSheetHeight by animateDpAsState(
                    targetValue = if (sheetExpanded) 1000.dp else 510.dp,
                    animationSpec = tween(durationMillis = 400),
                    label = "dynamicSheetHeight"
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(dynamicSheetHeight)
                        .clip(RoundedCornerShape(topStart = 70.dp, topEnd = 0.dp))
                        .zIndex(100f)
                        .offset(y = sheetHeight)
                        .background(Color(0xFF23272E))
                        .padding(bottom = 4.dp)
                        .graphicsLayer(alpha = sheetOpacity)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!isPayAnimationActive) {
                            // Top row: Drag handle (center) and expand/collapse button (right)
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
                                            .pointerInput(Unit) {
                                                detectDragGestures { change, dragAmount ->
                                                    if (dragAmount.y > 50) {
                                                        // Downward swipe: dismiss/collapse
                                                        showBottomSheet = false
                                                        sheetExpanded = false
                                                    } else if (dragAmount.y < -50) {
                                                        // Upward swipe: expand
                                                        sheetExpanded = true
                                                    }
                                                }
                                            }
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
                            PerksBottomSheet(
                                perks = perksOfAccountProduct,
                                navController = navController,
                                productId = card.accountProductId?.toString() ?: "",
                                accountId = card.id.toString()?: "",
                                onDismiss = {
                                    showBottomSheet = false
                                    sheetExpanded = false
                                    selectedCard = null
                                    cardAnimationTrigger = false
                                }
                            )
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

            // Pay Animation Counter - positioned at the bottom
            if (isPayAnimationActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp)
                        .zIndex(1000f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Back handle to cancel
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFF23272E), CircleShape)
                                .clickable {
                                    isPayAnimationActive = false
                                    waveAnimationKey = 0
                                    payAnimationSeconds = 59
                                    showBottomSheet = true // Show bottom sheet again
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Cancel payment",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Counter with circle background
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFF8EC5FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = payAnimationSeconds.toString(),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = RobotoFont
                            )
                        }
                    }
                }
            }

            // Wave animation - positioned absolutely in the main Box
            if (isPayAnimationActive) {
                val waveScale by animateFloatAsState(
                    targetValue = if (waveAnimationKey % 2 == 0) 2f else 1.5f,
                    animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
                    label = "waveScale"
                )
                val waveAlpha by animateFloatAsState(
                    targetValue = if (waveAnimationKey % 2 == 0) 0.3f else 0.6f,
                    animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
                    label = "waveAlpha"
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-70).dp)
                        .size(300.dp)
                        .graphicsLayer(
                            scaleX = waveScale,
                            scaleY = waveScale,
                            alpha = waveAlpha
                        )
                        .background(
                            Color(0xFF8EC5FF).copy(alpha = 0.6f),
                            CircleShape
                        )
                        .zIndex(100f)
                )
            }

            // Transaction Dialogs
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
                            if (amount <= BigDecimal.ZERO) "Amount must be greater than 0" else null
                        }
                    )
                }
            }
        }
    }
}
