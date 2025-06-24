package com.coded.capstone.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.composables.home.AccountCard
import com.coded.capstone.viewModels.AuthViewModel
import com.coded.capstone.viewModels.HomeScreenViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.coded.capstone.viewModels.AccountsUiState
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.enums.AccountType
import com.coded.capstone.respositories.UserRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalTime
import androidx.navigation.compose.rememberNavController
import com.coded.capstone.composables.home.DrawerContent
import com.coded.capstone.composables.home.EmptyAccountsCard
import com.coded.capstone.composables.home.ErrorStateCard
import com.coded.capstone.composables.home.RewardCard
import com.coded.capstone.navigation.NavRoutes
import kotlin.random.Random
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import com.coded.capstone.ui.AppBackground
import com.coded.capstone.ui.theme.AppTypography
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassMorphismBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF), // Center (light gray)
                        Color(0xFF0B0B18)  // Edge (dark gray)
                    ),
                    center = Offset(700f, 700f), // Approximate center, adjust as needed
                    radius = 1000f // Large enough to cover most screens
                )
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onNotificationClick: () -> Unit = {},
    onAccountClick: (String) -> Unit = {},
    onViewAllAccounts: () -> Unit = {},
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val kyc by viewModel.kyc.collectAsState()
    val userName = kyc?.let { "${it.firstName} ${it.lastName}" }

    // Separate accounts into reward cards and regular accounts
    val accounts = (accountsUiState as? AccountsUiState.Success)?.accounts
    val (rewardCards, regularAccounts) = remember(accounts) {
        accounts?.partition { account ->
            account.accountType == AccountType.CASHBACK.name
        } ?: (emptyList<AccountResponse>() to emptyList<AccountResponse>())
    }

    // Animation states
    var greetingVisible by remember { mutableStateOf(false) }
    var rewardCardVisible by remember { mutableStateOf(false) }
    val accountListVisibility = remember { mutableStateListOf<Boolean>() }

    LaunchedEffect(Unit) {
        greetingVisible = true
        rewardCardVisible = true
        // Staggered fade-in for account rows
        regularAccounts.forEachIndexed { index, _ ->
            accountListVisibility.add(false)
            kotlinx.coroutines.delay(80L)
            accountListVisibility[index] = true
        }
    }

    // Get time-based greeting
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    AppBackground {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    userName = userName ?: "...",
                    onProfileClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.NAV_ROUTE_PROFILE)
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings")
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top bar with hamburger menu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
//                                .background(
//                                    brush = Brush.linearGradient(
//                                        colors = listOf(
//                                            Color.White.copy(alpha = 0.1f),
//                                            Color.White.copy(alpha = 0.05f)
//                                        )
//                                    )
//                                )
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
//                                .background(
//                                    brush = Brush.linearGradient(
//                                        colors = listOf(
//                                            Color.White.copy(alpha = 0.1f),
//                                            Color.White.copy(alpha = 0.05f)
//                                        )
//                                    )
//                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Greeting Section
                            AnimatedVisibility(
                                visible = greetingVisible,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(600)
                                ) + fadeIn(animationSpec = tween(600)),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        if (userName == null) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(28.dp),
                                                color = Color.White
                                            )
                                        } else {
                                            Text(
                                                text = "$greeting, $userName",
                                                style = AppTypography.headlineMedium,
                                                fontWeight = FontWeight.Bold, fontSize = 30.sp,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = "Welcome back to KLUE",
                                            style = AppTypography.bodySmall, fontSize = 23.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }

                        // Reward Cards Section
                        if (rewardCards.isNotEmpty() && accountsUiState is AccountsUiState.Success) {
                            item {
                                AnimatedVisibility(
                                    visible = rewardCardVisible,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(700)
                                    ) + fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.95f),
                                ) {
                                    RewardCard(
                                        account = rewardCards.first(),
                                        onClick = {
                                            onAccountClick(rewardCards.first().id.toString())
                                            navController.navigate(NavRoutes.accountDetailRoute(rewardCards.first().id.toString()))
                                        }
                                    )
                                }
                            }
                        }

                        item {
                            // My Accounts Section Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "My Accounts",
                                    style = AppTypography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                TextButton(
                                    onClick = {
                                        onViewAllAccounts()
                                        navController.navigate("accounts")
                                    }
                                ) {
                                    Text(
                                        "View All",
                                        color = Color(0xFF8EC5FF)
                                    )
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF8EC5FF)
                                    )
                                }
                            }
                        }

                        // Handle different UI states for regular accounts
                        when (accountsUiState) {
                            is AccountsUiState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFF8EC5FF)
                                        )
                                    }
                                }
                            }
                            is AccountsUiState.Error -> {
                                item {
                                    val message = (accountsUiState as AccountsUiState.Error).message
                                    Box(Modifier.padding(horizontal = 16.dp)) {
                                        ErrorStateCard(
                                            message = message,
                                            onRetry = { viewModel.fetchAccounts() }
                                        )
                                    }
                                }
                            }
                            is AccountsUiState.Success -> {
                                // Show regular accounts (non-reward cards)
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clip(RoundedCornerShape(topStart = 70.dp))
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF151521).copy(alpha = 0.85f), // Center: dark
                                                        Color(0xFFE8E9EF).copy(alpha = 0.05f)  // Edge: light, almost transparent
                                                    ),
                                                    center = Offset(0f, 0f), // Top-left for a bleed effect
                                                    radius = 700f
                                                )
                                            )
                                    ) {
                                        Column {
                                            regularAccounts.forEachIndexed { index, account ->
                                                val rowAlpha by animateFloatAsState(
                                                    targetValue = if (accountListVisibility.getOrNull(index) == true) 1f else 0f,
                                                    animationSpec = tween(durationMillis = 400, delayMillis = index * 80)
                                                )
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(start = 40.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
                                                        .graphicsLayer { alpha = rowAlpha },
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = account.accountType?.replaceFirstChar { it.uppercase() } ?: "Account",
                                                            style = AppTypography.titleMedium,
                                                            color = Color.White,
                                                            fontSize = 18.sp
                                                        )
                                                        Text(
                                                            text = "•••• ${account.accountNumber?.takeLast(4)}",
                                                            style = AppTypography.bodySmall,
                                                            color = Color(0xFF8EC5FF)
                                                        )
                                                    }
                                                    Text(
                                                        text = "${String.format("%.3f", account.balance ?: 0.0)} KWD",
                                                        style = AppTypography.titleMedium,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                    )
                                                }
                                                Divider(
                                                    color = Color.White.copy(alpha = 0.1f),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                // If no regular accounts exist, show empty state
                                if (regularAccounts.isEmpty() && rewardCards.isEmpty()) {
                                    item {
                                        Box(Modifier.padding(horizontal = 16.dp)) {
                                            EmptyAccountsCard()
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}