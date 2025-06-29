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

    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FB))
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
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
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
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color(0xFF6A7477).copy(alpha = 0.85f))
                                            .blur(8.dp)
                                    )
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

                                Box {
                                    IconButton(
                                        onClick = { showNotifications = true },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF6A7477).copy(alpha = 0.85f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    if (unreadCount > 0) {
                                        Badge(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-4).dp, y = 4.dp),
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
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
                                                        color = Color(0xFF374151)
                                                    )
                                                } else {
                                                    Text(
                                                        text = "$greeting, $userName",
                                                        style = AppTypography.headlineMedium,
                                                        fontWeight = FontWeight.Bold, fontSize = 23.sp,
                                                        color = Color(0xFF23272E)
                                                    )
                                                }
                                                Text(
                                                    text = "Welcome back to KLUE",
                                                    style = AppTypography.bodySmall, fontSize = 18.sp,
                                                    color = Color(0xFF6B7280)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(45.dp))

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
                                                userXp = userXp,
                                                onClick = {
                                                    navController.navigate(NavRoutes.NAV_ROUTE_XP_HISTORY)
                                                }
                                            )
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(50.dp))

                                    // My Accounts Section Container (full width, dark gray, only top corners rounded)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .background(
                                                color = Color(0xFF23272E),
                                                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 24.dp, bottom = 0.dp)
                                        ) {
                                            // My Accounts Section Header
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    BankFillIcon(
                                                        modifier = Modifier.size(23.dp),
                                                        color = Color.White
                                                    )
                                                    Text(
                                                        text = "My Accounts",
                                                        fontSize = 23.sp,
                                                        style = AppTypography.headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(CircleShape)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .matchParentSize()
                                                            .background(Color(0xFF6A7477).copy(alpha = 0.85f))
                                                            .blur(8.dp)
                                                    )
                                                    IconButton(
                                                        onClick = { isAccountsExpanded = !isAccountsExpanded },
                                                        modifier = Modifier.matchParentSize()
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isAccountsExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                            contentDescription = if (isAccountsExpanded) "Collapse accounts" else "Expand accounts",
                                                            tint = Color(0xFF8EC5FF),
                                                            modifier = Modifier.size(32.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // The account list and its items will be rendered here, all inside this dark gray Box
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth().fillParentMaxHeight()
                                                    .background(Color(0xFF23272E))
                                            ) {
                                                Column {
                                                    Log.d("HomeScreen", "displayedAccounts size: ${displayedAccounts.size} - $displayedAccounts")
                                                    displayedAccounts.forEachIndexed { index, account ->
                                                        AnimatedVisibility(
                                                            visible = true, // your animation logic here
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clip(RoundedCornerShape(16.dp))
                                                                    .background(Color(0xFF23272E))
                                                                    .padding(horizontal = 0.dp, vertical = 0.dp)
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(72.dp)
                                                                        .clickable {
                                                                            onAccountClick(account.id.toString())
                                                                            navController.navigate(NavRoutes.accountDetailRoute(account.id.toString()))
                                                                        }
                                                                        .padding(start = 32.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
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
                                                                            color = Color.White
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
                                                            }
                                                        }
                                                    }
                                                }
                                            }
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
