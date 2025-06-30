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
import androidx.compose.material.icons.filled.Block
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.coded.capstone.respositories.UserRepository
import androidx.navigation.compose.rememberNavController
import com.coded.capstone.ui.theme.AppTypography
import androidx.compose.animation.EnterTransition
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import com.coded.capstone.respositories.AccountProductRepository

// Roboto font family
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

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
    // Source accounts: debit and cashback only (exclude credit)
    val sourceAccounts = allAccounts.filter { account ->
        val accountType = account.accountType?.lowercase()
        accountType == "debit" || accountType == "cashback"
    }

    // Dynamic destination accounts based on source account type
    val getDestinationAccounts = { sourceAccount: AccountResponse? ->
        when (sourceAccount?.accountType?.lowercase()) {
            "cashback" -> {
                // Cashback source can only transfer to debit accounts
                allAccounts.filter { account ->
                    account.accountType?.lowercase() == "debit" && account.id != sourceAccount.id
                }.distinctBy { it.id }
            }
            "debit" -> {
                // Debit source can transfer to debit or credit accounts (exclude cashback as destination)
                allAccounts.filter { account ->
                    val accountType = account.accountType?.lowercase()
                    (accountType == "debit" || accountType == "credit") && account.id != sourceAccount.id
                }.distinctBy { it.id }
            }
            "credit" -> {
                // Credit accounts cannot transfer to anything
                emptyList()
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

    // Initialize fromCard with a valid source account (debit or cashback)
    var fromCard by remember { mutableStateOf<AccountResponse?>(null) }

    // Set initial source card (debit or cashback account)
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
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    var currentToIndex by remember { mutableStateOf(0) }
    var isSwapping by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    var selectedCurrency by remember { mutableStateOf("KWD") }
    var currencyExpanded by remember { mutableStateOf(false) }
    
    // Animation and gesture states
    var isAnimating by remember { mutableStateOf(false) }
    var lastSwipeTime by remember { mutableStateOf(0L) }
    var totalDrag by remember { mutableStateOf(0f) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // Reset animation state after completion
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(450) // Wait for animation to complete (400ms + buffer)
            isAnimating = false
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        ),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
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
                                text = "No valid accounts available for transfers. You need at least one debit or cashback account to send transfers.",
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
                                    "cashback" -> "No debit accounts available for destination. Cashback accounts can only transfer to debit accounts."
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
                    .height(500.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.zIndex(0f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // From Card Stack
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Source account card stack
                        sourceAccounts.forEachIndexed { index, account ->
                            val isSelected = account.id == fromCard?.id
                            val baseOffset = if (isSelected) 0.dp else (index * 8).dp
                            val scale = if (isSelected) 1f else 0.95f - (index * 0.05f)
                            val alpha = if (isSelected) 1f else 0.7f - (index * 0.2f)
                            
                            // Get recommendation type for proper colors
                            val recommendationType = getRecommendationType(account)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .offset(y = baseOffset)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        this.alpha = alpha
                                    }
                                    .zIndex(if (isSelected) 1000f else 100f - index)
                                    .shadow(
                                        elevation = if (isSelected) 20.dp else 8.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.3f),
                                        spotColor = Color(0xFF8EC5FF).copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        fromCard = account
                                        // Reset destination index when source changes
                                        currentToIndex = 0
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                WalletCard(
                                    account = account,
                                    onCardClick = { /* Handled by parent clickable */ },
                                    modifier = Modifier.fillMaxSize(),
                                    recommendationType = recommendationType
                                )
                            }
                        }

                        // "From" label
                        Text(
                            text = "FROM",
                            color = Color(0xFF6B7280),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 16.dp, top = 8.dp)
                                .background(
                                    Color.White.copy(alpha = 0.9f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // To Card Stack
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .pointerInput(availableDestinations.size, isAnimating) {
                                detectHorizontalDragGestures(
                                    onDragStart = {
                                        totalDrag = 0f
                                    },
                                    onDragEnd = {
                                        // Only process gesture on drag end to prevent multiple triggers
                                        val currentTime = System.currentTimeMillis()
                                        val threshold = 100.dp.toPx() // Increased threshold for better reliability
                                        
                                        if (!isSwapping && 
                                            !isAnimating && 
                                            availableDestinations.size > 1 && 
                                            currentTime - lastSwipeTime > 500 // Debounce: minimum 500ms between swipes
                                        ) {
                                            if (kotlin.math.abs(totalDrag) > threshold) {
                                                lastSwipeTime = currentTime
                                                isAnimating = true
                                                
                                                if (totalDrag < 0) {
                                                    // Swipe left - go to next destination
                                                    currentToIndex = (currentToIndex + 1) % availableDestinations.size
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                } else {
                                                    // Swipe right - go to previous destination
                                                    currentToIndex = if (currentToIndex > 0) {
                                                        currentToIndex - 1
                                                    } else {
                                                        availableDestinations.size - 1
                                                    }
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                }
                                            }
                                        }
                                    }
                                ) { _, dragAmount -> 
                                    // Accumulate total drag distance
                                    totalDrag += dragAmount
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (availableDestinations.isNotEmpty()) {
                            // Horizontal carousel for destination cards
                            availableDestinations.forEachIndexed { index, account ->
                                // Get recommendation type for proper colors
                                val recommendationType = getRecommendationType(account)
                                
                                // Calculate position relative to current index
                                val positionOffset = index - currentToIndex
                                
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
                                        WalletCard(
                                            account = account,
                                            onCardClick = { /* Handled by drag */ },
                                            modifier = Modifier.fillMaxSize(),
                                            recommendationType = recommendationType
                                        )
                                    }
                                }
                            }

                            // "TO" label
                            Text(
                                text = "TO",
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 16.dp, top = 8.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            // Swipe arrows - only show when multiple destinations are available
                            if (availableDestinations.size > 1) {
                                // Left arrow hint - only show if not at the start
                                if (currentToIndex > 0) {
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
                                if (currentToIndex < availableDestinations.size - 1) {
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

                            // Swipe indicator
                            if (availableDestinations.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    availableDestinations.forEachIndexed { index, _ ->
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    if (index == currentToIndex) Color(0xFF8EC5FF) else Color(0xFFD1D5DB),
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            // No destination available
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = "No destination",
                                            tint = Color(0xFF9CA3AF),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No valid destination",
                                            color = Color(0xFF6B7280),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = when (fromCard?.accountType?.lowercase()) {
                                                "cashback" -> "Cashback can only transfer to debit"
                                                "debit" -> "Debit can transfer to debit or credit"
                                                else -> "Select a valid source account"
                                            },
                                            color = Color(0xFF9CA3AF),
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Swap Button (only show if both cards are selected and swap is valid)
                if (fromCard != null && toCard != null && sourceAccounts.contains(toCard) && getDestinationAccounts(toCard).contains(fromCard)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .zIndex(1001f)
                            .background(Color(0xFF23272E), shape = RoundedCornerShape(32.dp))
                            .border(4.dp, Color.White, shape = RoundedCornerShape(32.dp))
                            .clickable {
                                if (!isSwapping) {
                                    coroutineScope.launch {
                                        isSwapping = true
                                        delay(300)
                                        val temp = fromCard
                                        fromCard = toCard
                                        currentToIndex = getDestinationAccounts(fromCard).indexOf(temp).coerceAtLeast(0)
                                        delay(300)
                                        isSwapping = false
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

            // Transfer Button - only show if source is not a credit account
            if (fromCard?.accountType?.lowercase() != "credit") {
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