package com.coded.capstone.wallet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coded.capstone.wallet.components.CardPerksBottomSheet
import com.coded.capstone.wallet.components.CardStack
import com.coded.capstone.wallet.components.QuickActionButtons
import com.coded.capstone.wallet.components.TransactionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onNavigateToAccountDetails: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { WalletViewModel(context) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadAccounts()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF0A0A0A),
                        Color(0xFF000000)
                    ),
                    radius = 1500f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back navigation
            WalletHeader(onNavigateBack = onNavigateBack)

            // Main Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> LoadingState()
                    !uiState.error.isNullOrEmpty() -> ErrorState(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadAccounts() }
                    )
                    uiState.accounts.isEmpty() -> EmptyWalletState()
                    else -> {
                        // Card Stack using CardStack component
                        CardStack(
                            cards = uiState.accounts,
                            selectedCardId = uiState.selectedAccountId,
                            onCardSelected = { cardId ->
                                if (cardId == null) {
                                    viewModel.deselectCard()
                                } else {
                                    viewModel.selectCard(cardId)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Quick Action Buttons show if card is selected
        AnimatedVisibility(
            visible = uiState.selectedAccountId != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 800,
                    delayMillis = 300,
                    easing = EaseOutCubic
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(400, easing = EaseInCubic)
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 280.dp)
        ) {
            uiState.selectedAccount?.let { selectedAccount ->
                QuickActionButtons(
                    selectedAccount = selectedAccount,
                    allAccounts = uiState.accounts,
                    onPayClick = { /* TODO: NFC payment */ },
                    onTopUpClick = { viewModel.showTopUpDialog() },
                    onTransferClick = { viewModel.showTransferDialog() },
                    onViewDetailsClick = {
                        onNavigateToAccountDetails(selectedAccount.accountNumber)
                    }
                )
            }
        }

        // Bottom Sheet with card perks
        CardPerksBottomSheet(
            selectedCard = uiState.selectedAccount
        )

        // Transaction Dialogs
        if (uiState.showTopUpDialog) {
            TransactionDialog(
                title = "Top Up Account",
                fromLabel = "From:",
                accounts = uiState.accounts.filter { account ->
                    account.id != uiState.selectedAccountId &&
                            account.canTransfer && account.isActive
                },
                selectedAccount = uiState.selectedSourceAccount,
                amount = uiState.topUpAmount,
                isLoading = uiState.isProcessingTransaction,
                error = uiState.transactionError,
                onAccountSelected = { viewModel.selectSourceAccount(it) },
                onAmountChanged = { viewModel.updateTopUpAmount(it) },
                onConfirm = {
                    uiState.selectedSourceAccount?.let { from ->
                        uiState.selectedAccount?.let { to ->
                            viewModel.topUpAccount(from.accountNumber, to.accountNumber, uiState.topUpAmount)
                        }
                    }
                },
                onDismiss = { viewModel.hideTopUpDialog() }
            )
        }

        if (uiState.showTransferDialog) {
            TransactionDialog(
                title = "Transfer Funds",
                fromLabel = "To:",
                accounts = uiState.accounts.filter { account ->
                    account.id != uiState.selectedAccountId && account.isActive
                },
                selectedAccount = uiState.selectedDestinationAccount,
                amount = uiState.transferAmount,
                isLoading = uiState.isProcessingTransaction,
                error = uiState.transactionError,
                onAccountSelected = { viewModel.selectDestinationAccount(it) },
                onAmountChanged = { viewModel.updateTransferAmount(it) },
                onConfirm = {
                    uiState.selectedDestinationAccount?.let { to ->
                        uiState.selectedAccount?.let { from ->
                            viewModel.transferFunds(from.accountNumber, to.accountNumber, uiState.transferAmount)
                        }
                    }
                },
                onDismiss = { viewModel.hideTransferDialog() }
            )
        }
    }
}

@Composable
private fun WalletHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Wallet",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Light,
            color = Color.White
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFFD700),
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
private fun EmptyWalletState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = "Empty wallet",
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Cards Available",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You don't have any active accounts yet. Contact your bank to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}