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
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import com.coded.capstone.SVG.BankFillIcon
import androidx.compose.foundation.layout.Spacer as Spacer
import android.util.Log
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.ui.draw.shadow
import androidx.activity.compose.BackHandler

import com.coded.capstone.Screens.notifications.NotificationCenter
import com.coded.capstone.viewModels.NotificationViewModel


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
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(context) as T
            }
        }
    }
    val viewModel: HomeScreenViewModel = viewModel(factory = factory)
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val kyc by viewModel.kyc.collectAsState()
    val userName = kyc?.let { "${it.firstName} ${it.lastName}" }
    val userXp by viewModel.userXp.collectAsState()

    // Notification integration
    var showNotifications by remember { mutableStateOf(false) }
    val notificationViewModel: NotificationViewModel = viewModel { NotificationViewModel(context) }
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    // Fetch accounts when HomeScreen loads
    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "LaunchedEffect for fetchAccounts triggered")
        viewModel.fetchAccounts()
    }

    // Fetch user XP info when screen loads
    LaunchedEffect(Unit) {
        viewModel.getUserXpInfo()
    }

    // Fetch notifications when screen loads
    LaunchedEffect(Unit) {
        notificationViewModel.fetchNotifications()
    }

    // Separate accounts into reward cards and regular accounts
    val accounts = (accountsUiState as? AccountsUiState.Success)?.accounts
    val (rewardCards, regularAccounts) = remember(accounts) {
        accounts?.partition { account ->
            account.accountType == AccountType.CASHBACK.name
        } ?: (emptyList<AccountResponse>() to emptyList<AccountResponse>())
    }

    // State to control the expanded state of the accounts list
    var isAccountsExpanded by remember { mutableStateOf(false) }

    // Display only 3 accounts by default
    val displayedAccounts = if (isAccountsExpanded) {
        regularAccounts
    } else {
        regularAccounts.take(3)
    }

    // Animation states
    var greetingVisible by remember { mutableStateOf(false) }
    var rewardCardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        greetingVisible = true
        rewardCardVisible = true
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

    // Handle back button press to close expanded accounts list
    BackHandler(enabled = isAccountsExpanded) {
        isAccountsExpanded = false
    }

    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFE5E7EB), // Light silver
                            Color(0xFFD1D5DB)  // Silver
                        )
                    )
                )
        ) {
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),  // Remove default window insets
                    topBar = {
                        // Remove the top bar spacer to allow content to go behind status bar
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                        // Remove top padding to extend behind status bar
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 1. TOP CONTAINER - Dark gray with rounded bottom edges (extends to top)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF23272E),
                                        shape = RoundedCornerShape(bottomStart = 70.dp, bottomEnd = 0.dp)
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 50.dp + paddingValues.calculateTopPadding(), // Add status bar height
                                            bottom = 20.dp
                                        )
                                ) {
                                    // Top bar with hamburger menu and notification (brand blue color)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF8EC5FF))
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    scope.launch { drawerState.open() }
                                                },
                                                modifier = Modifier.matchParentSize()
                                            ) {
                                                Icon(
                                                    Icons.Default.Menu,
                                                    contentDescription = "Menu",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF8EC5FF))
                                        ) {
                                            IconButton(
                                                onClick = { showNotifications = true },
                                                modifier = Modifier.matchParentSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = "Notifications",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(45.dp))

                                    // Greeting Section (brand blue color)
                                    AnimatedVisibility(
                                        visible = greetingVisible,
                                        enter = slideInHorizontally(
                                            initialOffsetX = { -it },
                                            animationSpec = tween(600)
                                        ) + fadeIn(animationSpec = tween(600)),
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(start = 24.dp)
                                        ) {
                                            if (userName == null) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(28.dp),
                                                    color = Color(0xFF8EC5FF)
                                                )
                                            } else {
                                                Text(
                                                    text = "$greeting, $userName",
                                                    style = AppTypography.headlineMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 23.sp,
                                                    color = Color.White
                                                )
                                            }
                                            Text(
                                                text = "Welcome back to KLUE",
                                                style = AppTypography.bodySmall,
                                                fontSize = 18.sp,
                                                color = Color(0xFF8EC5FF).copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. CONTENT BELOW GREETINGS - Everything in LazyColumn
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(bottom = 60.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                // Accounts Section
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Transparent)
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                                ambientColor = Color.Black.copy(alpha = 0.1f),
                                                spotColor = Color.Black.copy(alpha = 0.15f)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 24.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // My Accounts Section Header
                                            Row(
                                                modifier = Modifier.fillMaxWidth().background(Color.Transparent),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    BankFillIcon(
                                                        modifier = Modifier.size(23.dp),
                                                        color = Color(0xFF23272E)
                                                    )
                                                    Text(
                                                        text = "My Accounts",
                                                        fontSize = 23.sp,
                                                        style = AppTypography.headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF23272E)
                                                    )
                                                }
                                            }

                                            // Handle different UI states for regular accounts
                                            when (accountsUiState) {
                                                is AccountsUiState.Loading -> {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator(
                                                            color = Color(0xFF8EC5FF)
                                                        )
                                                    }
                                                }
                                                is AccountsUiState.Error -> {
                                                    val message = (accountsUiState as AccountsUiState.Error).message
                                                    ErrorStateCard(
                                                        message = message,
                                                        onRetry = { viewModel.fetchAccounts() }
                                                    )
                                                }
                                                is AccountsUiState.Success -> {
                                                    // If no regular accounts exist, show empty state
                                                    if (regularAccounts.isEmpty() && rewardCards.isEmpty()) {
                                                        EmptyAccountsCard()
                                                    } else {
                                                        // Account list
                                                        displayedAccounts.forEach { account ->
                                                            Card(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                                                shape = RoundedCornerShape(16.dp),
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = Color(0xFF23272E) // Matching transaction history color
                                                                ),
                                                                elevation = CardDefaults.cardElevation(
                                                                    defaultElevation = 4.dp
                                                                )
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .clickable {
                                                                            onAccountClick(account.id.toString())
                                                                            navController.navigate(NavRoutes.accountDetailRoute(account.id.toString()))
                                                                        }
                                                                        .padding(16.dp),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    // Account icon and details
                                                                    Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        modifier = Modifier.weight(1f)
                                                                    ) {
                                                                        // Icon with background
                                                                        Box(
                                                                            modifier = Modifier
                                                                                .size(44.dp)
                                                                                .background(
                                                                                    color = Color(0xFF8EC5FF).copy(alpha = 0.15f),
                                                                                    shape = CircleShape
                                                                                ),
                                                                            contentAlignment = Alignment.Center
                                                                        ) {
                                                                            BankFillIcon(
                                                                                modifier = Modifier.size(20.dp),
                                                                                color = Color(0xFF8EC5FF)
                                                                            )
                                                                        }

                                                                        Spacer(modifier = Modifier.width(16.dp))

                                                                        // Account details
                                                                        Column {
                                                                            Text(
                                                                                text = account.accountType?.replaceFirstChar { it.uppercase() } ?: "Account",
                                                                                style = AppTypography.bodyLarge.copy(
                                                                                    fontWeight = FontWeight.SemiBold,
                                                                                    fontSize = 16.sp
                                                                                ),
                                                                                color = Color.White
                                                                            )
                                                                            Spacer(modifier = Modifier.height(4.dp))
                                                                            Text(
                                                                                text = "•••• ${account.accountNumber?.takeLast(4)}",
                                                                                style = AppTypography.bodySmall.copy(
                                                                                    fontSize = 12.sp
                                                                                ),
                                                                                color = Color.Gray
                                                                            )
                                                                        }
                                                                    }

                                                                    // Balance
                                                                    Column(
                                                                        horizontalAlignment = Alignment.End
                                                                    ) {
                                                                        Text(
                                                                            text = "${String.format("%.3f", account.balance ?: 0.0)} KWD",
                                                                            style = AppTypography.bodyLarge.copy(
                                                                                fontWeight = FontWeight.Bold,
                                                                                fontSize = 16.sp
                                                                            ),
                                                                            color = Color(0xFF8EC5FF)
                                                                        )
                                                                        Spacer(modifier = Modifier.height(4.dp))
                                                                        Text(
                                                                            text = "Available",
                                                                            style = AppTypography.bodySmall.copy(
                                                                                fontSize = 12.sp
                                                                            ),
                                                                            color = Color.Gray
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        // Show All button - only show if there are more than 3 accounts
                                                        if (regularAccounts.size > 3) {
                                                            Card(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(horizontal = 4.dp, vertical = 0.dp),
                                                                shape = RoundedCornerShape(16.dp),
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = Color.Transparent
                                                                ),
                                                                elevation = CardDefaults.cardElevation(
                                                                    defaultElevation = 0.dp
                                                                )
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .clickable {
                                                                            isAccountsExpanded = !isAccountsExpanded
                                                                        }
                                                                        .padding(start=8.dp, end = 8.dp),
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        text = if (isAccountsExpanded) "Show Less" else "Show All",
                                                                        style = AppTypography.bodyLarge.copy(
                                                                            fontWeight = FontWeight.Medium,
                                                                            fontSize = 16.sp
                                                                        ),
                                                                        color = Color(0xFF8EC5FF)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Icon(
                                                                        imageVector = if (isAccountsExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                                        contentDescription = if (isAccountsExpanded) "Show less accounts" else "Show all accounts",
                                                                        tint = Color(0xFF8EC5FF),
                                                                        modifier = Modifier.size(24.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Rewards Section
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
                                                userXp = userXp,
                                                onClick = {
                                                    navController.navigate(NavRoutes.NAV_ROUTE_XP_HISTORY)
                                                }
                                            )
                                        }
                                    }
                                }

                                // Bottom Spacer
                                item {
                                    Spacer(modifier = Modifier.height(60.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    NotificationCenter(
        isVisible = showNotifications,
        onClose = { showNotifications = false },
        navController = navController
    )
}
