package com.coded.capstone.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.composables.home.AccountCard
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.AccountsUiState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.fadeIn
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.coded.capstone.composables.perks.EnhancedPerkItem
import com.coded.capstone.composables.ui.ActionButton
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {},
    onPayAction: (AccountResponse) -> Unit = {},
    onTransferAction: (AccountResponse) -> Unit = {},
    onDetailsAction: (AccountResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel: HomeScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(context) as T
            }
        }
    )

    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val accounts = (accountsUiState as? AccountsUiState.Success)?.accounts
    val perksOfAccountProduct by viewModel.perksOfAccountProduct.collectAsState()

    var selectedCard by remember { mutableStateOf<AccountResponse?>(null) }
    var currentCardIndex by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Pager state for card navigation
    val pagerState = rememberPagerState(pageCount = { accounts?.size ?: 0 })

    LaunchedEffect(selectedCard) {
        selectedCard?.let { card ->
            card.accountProductId?.let { productId ->
                viewModel.fetchPerksOfAccountProduct(productId.toString())
            }
        }
    }

    // Update current card index when pager changes
    LaunchedEffect(pagerState.currentPage) {
        currentCardIndex = pagerState.currentPage
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
                    if (accounts?.isNotEmpty() == true) {
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1F1F23)
                        ),
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
                                onClick = { viewModel.fetchAccounts() },
                                modifier = Modifier.padding(top = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B5CF6)
                                )
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }
                is AccountsUiState.Success -> {
                    if (accounts!!.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1F1F23)
                            ),
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
                    } else {
                        // Enhanced Stacked Cards with Swipe Navigation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val account = accounts[page]

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp)
                                ) {
                                    // Stack effect for non-active cards
                                    accounts.take(page + 1).forEachIndexed { index, stackAccount ->
                                        val isActive = index == page
                                        val offsetY = if (isActive) 0.dp else ((page - index) * 8).dp
                                        val scale = if (isActive) 1f else (1f - ((page - index) * 0.03f))
                                        val alpha = if (isActive) 1f else (1f - ((page - index) * 0.2f)).coerceAtLeast(0.3f)

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .offset(y = offsetY)
                                                .graphicsLayer {
                                                    scaleX = scale
                                                    scaleY = scale
                                                    this.alpha = alpha
                                                }
                                                .zIndex(index.toFloat())
                                                .shadow(
                                                    elevation = if (isActive) 12.dp else 8.dp,
                                                    shape = RoundedCornerShape(20.dp),
                                                    ambientColor = Color.Black.copy(alpha = 0.4f),
                                                    spotColor = Color.Black.copy(alpha = 0.4f)
                                                )
                                        ) {
                                            if (isActive) {
                                                AccountCard(
                                                    account = account,
                                                    onCardClick = {
                                                        selectedCard = account
                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clickable {
                                                            selectedCard = account
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Swipe indicator
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(accounts.size) { index ->
                                    val isActive = index == pagerState.currentPage
                                    Box(
                                        modifier = Modifier
                                            .size(if (isActive) 24.dp else 8.dp, 4.dp)
                                            .background(
                                                if (isActive) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.3f),
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Swipe Hint
                        if (accounts.size > 1) {
                            Text(
                                text = "← Swipe to see other cards →",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Enhanced Action Buttons Row
                        if (currentCardIndex < accounts.size) {
                            val currentAccount = accounts[currentCardIndex]

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Pay Button
                                ActionButton(
                                    icon = Icons.Default.Payment,
                                    label = "Pay",
                                    color = Color(0xFF10B981),
                                    onClick = {
                                        onPayAction(currentAccount)
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                // Transfer Button
                                ActionButton(
                                    icon = Icons.Default.SwapHoriz,
                                    label = "Transfer",
                                    color = Color(0xFF8B5CF6),
                                    onClick = {
                                        onTransferAction(currentAccount)
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                // Details Button
                                ActionButton(
                                    icon = Icons.Default.Info,
                                    label = "Details",
                                    color = Color(0xFF3B82F6),
                                    onClick = {
                                        onDetailsAction(currentAccount)
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Enhanced Bottom Sheet for Perks
        if (selectedCard != null) {
            val card = selectedCard!!
            var expanded by remember { mutableStateOf(false) }

            BackHandler(enabled = true) {
                selectedCard = null
            }

            ModalBottomSheet(
                onDismissRequest = { selectedCard = null },
                sheetState = sheetState,
                containerColor = Color(0xFF0F0F10),
                scrimColor = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(if (expanded) 0.9f else 0.6f)
                        .padding(24.dp)
                ) {
                    // Header with close and expand buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Account Details & Perks",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = Color.White
                        )

                        Row {
                            IconButton(
                                onClick = { expanded = !expanded },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (expanded) "Collapse" else "Expand",
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { selectedCard = null },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.1f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Enhanced Account info header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1D)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = card.ownerType ?: "Account #${card.accountNumber?.takeLast(4) ?: "****"}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${card.balance} KWD",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Account Number: ****${card.accountNumber?.takeLast(4) ?: "****"}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons in Bottom Sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionButton(
                            icon = Icons.Default.Payment,
                            label = "Pay",
                            color = Color(0xFF10B981),
                            onClick = {
                                onPayAction(card)
                                selectedCard = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ActionButton(
                            icon = Icons.Default.SwapHoriz,
                            label = "Transfer",
                            color = Color(0xFF8B5CF6),
                            onClick = {
                                onTransferAction(card)
                                selectedCard = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ActionButton(
                            icon = Icons.Default.Info,
                            label = "Details",
                            color = Color(0xFF3B82F6),
                            onClick = {
                                onDetailsAction(card)
                                selectedCard = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Perks List
                    if (perksOfAccountProduct.isNotEmpty()) {
                        Text(
                            text = "Account Perks",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(perksOfAccountProduct) { index, perk ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(
                                        animationSpec = tween(400, delayMillis = index * 100)
                                    ) + slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(400, delayMillis = index * 100)
                                    )
                                ) {
                                    EnhancedPerkItem(perk = perk)
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        Color(0xFF8B5CF6).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No perks available",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "This account doesn't have any special perks yet.",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}




