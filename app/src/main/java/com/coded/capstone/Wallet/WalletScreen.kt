package com.coded.capstone.Wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.composables.home.AccountCard
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.AccountsUiState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.SVG.TransferUsersIcon
import com.coded.capstone.SVG.CreditCardCloseIcon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.painterResource
import com.coded.capstone.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {}
) {
    val context = LocalContext.current
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Fetch perks when a card is selected
    LaunchedEffect(selectedCard) {
        selectedCard?.let { card ->
            card.accountProductId?.let { productId ->
                viewModel.fetchPerksOfAccountProduct(productId.toString())
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
            // Header with gradient text effect
            Text(
                text = "My Wallet",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
            )

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
                        // Stacked Cards Animation
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy((-20).dp)
                        ) {
                            items(accounts.reversed()) { account ->
                                val index = accounts.reversed().indexOf(account)
                                val offsetY = (index * 20).dp
                                val scale = 1f - (index * 0.05f)
                                val alpha = 1f - (index * 0.15f)

                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(600, delayMillis = index * 100)) +
                                            scaleIn(animationSpec = tween(600, delayMillis = index * 100))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(y = offsetY)
                                            .graphicsLayer {
                                                scaleX = scale
                                                scaleY = scale
                                                this.alpha = alpha.coerceAtLeast(0.3f)
                                            }
                                            .zIndex((accounts.size - index).toFloat())
                                            .shadow(
                                                elevation = (8 + index * 4).dp,
                                                shape = RoundedCornerShape(20.dp),
                                                ambientColor = Color.Black.copy(alpha = 0.3f),
                                                spotColor = Color.Black.copy(alpha = 0.3f)
                                            )
                                    ) {
                                        AccountCard(
                                            account = account,
                                            onCardClick = { selectedCard = account },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedCard = account }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height((accounts.size * 20 + 100).dp))

                        // Enhanced Services Row
                        AnimatedVisibility(
                            visible = selectedCard != null,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeIn(animationSpec = tween(600))
                        ) {
                            ServicesRow(
                                onTransfer = { /* handle transfer */ },
                                onTransferToOthers = { /* handle transfer to others */ },
                                onCloseAccount = { /* handle close account */ }
                            )
                        }
                    }
                }
            }
        }

        // Enhanced Bottom Sheet for Perks Only
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
                    // Header with expand button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Account Perks",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.White
                        )

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
                                painter = painterResource(
                                    id = if (expanded) R.drawable.baseline_keyboard_double_arrow_down_24
                                    else R.drawable.baseline_keyboard_double_arrow_up_24
                                ),
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Account info header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1D)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
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
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${card.balance} KWD",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Perks List
                    if (perksOfAccountProduct.isNotEmpty()) {
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

@Composable
fun ServicesRow(
    onTransfer: () -> Unit,
    onTransferToOthers: () -> Unit,
    onCloseAccount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1D).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            EnhancedServiceButton(
                icon = { CardTransferBoldIcon(modifier = Modifier.size(24.dp)) },
                label = "Transfer",
                color = Color(0xFF8B5CF6),
                onClick = onTransfer
            )
            EnhancedServiceButton(
                icon = { TransferUsersIcon(modifier = Modifier.size(24.dp)) },
                label = "Send",
                color = Color(0xFF10B981),
                onClick = onTransferToOthers
            )
            EnhancedServiceButton(
                icon = { CreditCardCloseIcon(modifier = Modifier.size(24.dp)) },
                label = "Close",
                color = Color(0xFFEF4444),
                onClick = onCloseAccount
            )
        }
    }
}

@Composable
private fun EnhancedServiceButton(
    icon: @Composable () -> Unit,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .scale(scale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.3f),
                            color.copy(alpha = 0.1f)
                        )
                    ),
                    CircleShape
                )
                .padding(16.dp)
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EnhancedPerkItem(perk: PerkDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1D)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Perk icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    getPerkColor(perk.type).copy(alpha = 0.3f),
                                    getPerkColor(perk.type).copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPerkIcon(perk.type),
                        contentDescription = perk.type,
                        tint = getPerkColor(perk.type),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Perk details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = perk.type?.replaceFirstChar { it.uppercase() } ?: "Unknown Perk",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    perk.perkAmount?.let { amount ->
                        Text(
                            text = if (perk.type?.contains("cashback", ignoreCase = true) == true) {
                                "${amount}% Cashback"
                            } else if (perk.type?.contains("discount", ignoreCase = true) == true) {
                                "${amount}% Discount"
                            } else {
                                "Amount: $amount"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = getPerkColor(perk.type),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    perk.minPayment?.let { minPayment ->
                        Text(
                            text = "Min. Payment: $minPayment KWD",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Rewards XP Badge
                perk.rewardsXp?.let { xp ->
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFFFD700).copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+$xp XP",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }

            // Enhanced categories display
            if (!perk.categories.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Applicable Categories:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    perk.categories.take(3).forEach { category ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category.name ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8B5CF6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (perk.categories.size > 3) {
                        Text(
                            text = "+${perk.categories.size - 3} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Tier indicator
            if (perk.isTierBased == true) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF10B981).copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏆 Tier Based Rewards",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun getPerkColor(type: String?): Color {
    return when (type?.lowercase()) {
        "cashback" -> Color(0xFF10B981)
        "discount" -> Color(0xFF8B5CF6)
        "points" -> Color(0xFFFFD700)
        "rewards" -> Color(0xFFEF4444)
        else -> Color(0xFF06B6D4)
    }
}

private fun getPerkIcon(type: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type?.lowercase()) {
        "cashback" -> Icons.Default.AttachMoney
        "discount" -> Icons.Default.LocalOffer
        "points", "rewards" -> Icons.Default.Star
        else -> Icons.Default.CardGiftcard
    }
}