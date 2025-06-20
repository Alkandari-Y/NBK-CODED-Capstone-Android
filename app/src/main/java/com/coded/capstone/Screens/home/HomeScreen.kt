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
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
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
    val lazyListStateAccounts = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var totalBalance by remember { mutableStateOf(BigDecimal.ZERO) }
    val kyc = UserRepository.kyc
    val userName = "${kyc?.firstName} ${kyc?.lastName}"

    // Get time-based greeting
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    // Separate accounts into reward cards and regular accounts
    val accounts = (accountsUiState as? AccountsUiState.Success)?.accounts
    val (rewardCards, regularAccounts) = remember(accounts) {
        accounts?.partition { account ->
            account.accountType == AccountType.CASHBACK.name
        } ?: (emptyList<AccountResponse>() to emptyList<AccountResponse>())
    }

    // Calculate total balance when accounts change
//    LaunchedEffect(accounts) {
//        if (accounts != null) {
//            totalBalance = accounts.sumOf { it.balance }
//        }
//    }

    LaunchedEffect(Unit) {
        viewModel.fetchAccounts()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userName,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Top bar with hamburger menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    }
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color(0xFF666666)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Greeting Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$greeting, $userName",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C3E50)
                            )
                            Text(
                                text = "Welcome back to KLUE",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF7F8C8D)
                            )
                        }
                    }
                }

                // Reward Cards Section
                if (rewardCards.isNotEmpty() && accountsUiState is AccountsUiState.Success) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reward Cards",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C3E50)
                                )
                                if (rewardCards.size > 1) {
                                    TextButton(
                                        onClick = { navController.navigate("reward_cards") }
                                    ) {
                                        Text(
                                            "View All",
                                            color = Color(0xFFFFD700)
                                        )
                                        Icon(
                                            Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(rewardCards) { rewardCard ->
                                RewardCard(
                                    account = rewardCard,
                                    onClick = {
                                        onAccountClick(rewardCard.id.toString())
                                        navController.navigate(NavRoutes.accountDetailRoute(rewardCard.id.toString()))
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // My Accounts Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Accounts",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        TextButton(
                            onClick = {
                                onViewAllAccounts()
                                navController.navigate("accounts")
                            }
                        ) {
                            Text(
                                "View All",
                                color = Color(0xFF1976D2)
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF1976D2)
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
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is AccountsUiState.Error -> {
                        item {
                            val message = (accountsUiState as AccountsUiState.Error).message
                            ErrorStateCard(
                                message = message,
                                onRetry = { viewModel.fetchAccounts() }
                            )
                        }
                    }
                    is AccountsUiState.Success -> {
                        // Show regular accounts (non-reward cards)
                        items(regularAccounts) { account ->
                            AccountCard(
                                account = account,
                                onCardClick = { onAccountClick(account.id.toString()) },
                                modifier = Modifier.clickable {
                                    navController.navigate(NavRoutes.accountDetailRoute(account.id.toString()))
                                }
                            )
                        }
                        // If no regular accounts exist, show empty state
                        if (regularAccounts.isEmpty() && rewardCards.isEmpty()) {
                            item {
                                EmptyAccountsCard()
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








