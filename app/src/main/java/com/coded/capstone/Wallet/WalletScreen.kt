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
import com.coded.capstone.Wallet.components.CardStack
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.enums.AccountType
import java.math.BigDecimal
import com.coded.capstone.SVG.CardTransferBoldIcon
import com.coded.capstone.SVG.TransferUsersIcon
import com.coded.capstone.SVG.CreditCardCloseIcon
import androidx.compose.material.icons.filled.Info
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.res.painterResource
import com.coded.capstone.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.activity.compose.BackHandler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {}
) {
    // Sample data - replace with actual API calls
    val sampleAccounts = remember {
        listOf(
            AccountResponse(
                accountNumber = "1234567890123456",
                id = 1L,
                balance = BigDecimal("1250.50"),
                name = "Main Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.DEBIT
            ),
            AccountResponse(
                accountNumber = "9876543210987654",
                id = 2L,
                balance = BigDecimal("500.25"),
                name = "Savings Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.CREDIT
            ),
            AccountResponse(
                accountNumber = "5555666677778888",
                id = 3L,
                balance = BigDecimal("2500.00"),
                name = "Investment Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.DEBIT
            )
        )
    }
    
    var selectedCard by remember { mutableStateOf<AccountResponse?>(null) }
    var showAccountNumber by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Details, 1 = Transactions
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var showStack by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showStack = false
        showStack = true
    }

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Wallet") },
//                actions = {
//                    IconButton(onClick = onNavigateToMap) {
//                        Icon(Icons.Default.Map, contentDescription = "Map")
//                    }
//                    IconButton(onClick = { /* Add new account */ }) {
//                        Icon(Icons.Default.Add, contentDescription = "Add Account")
//                    }
//                }
//            )
//        }
//    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Card Stack (bigger size)
            AnimatedVisibility(
                visible = showStack,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400))
            ) {
                CardStack(
                    accounts = sampleAccounts,
                    selectedCard = selectedCard,
                    onCardSelected = { selectedCard = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 0.dp),
                    showAccountNumber = showAccountNumber
                )
            }
            
            // Show services row above details card when a card is selected, with animation
            AnimatedVisibility(
                visible = selectedCard != null,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400))
            ) {
                ServicesRow(
                    onTransfer = { /* handle transfer */ },
                    onTransferToOthers = { /* handle transfer to others */ },
                    onCloseAccount = { /* handle close account */ }
                )
            }
            
            // Show bottom sheet when a card is selected
            if (selectedCard != null) {
                val card = selectedCard!!
                var expanded by remember { mutableStateOf(false) }

                BackHandler(enabled = true) {
                    selectedCard = null
                }

                ModalBottomSheet(
                    onDismissRequest = { selectedCard = null },
                    sheetState = sheetState,
                    containerColor = Color.Black,
                    scrimColor = Color.Transparent,
                    shape = RoundedCornerShape(topStart = 70.dp, topEnd = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(if (expanded) 0.99f else 0.50f)
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clickable { expanded = !expanded },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = if (expanded) R.drawable.baseline_keyboard_double_arrow_down_24 else R.drawable.baseline_keyboard_double_arrow_up_24),
                                    contentDescription = if (expanded) "Collapse" else "Expand",
                                    tint = Color(0xFF8AAEBD),
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            indicator = { tabPositions: List<TabPosition> ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = Color(0xFF8AAEBD)
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Details", color = if (selectedTab == 0) Color.White else Color.Gray) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Transactions", color = if (selectedTab == 1) Color.White else Color.Gray) }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        if (selectedTab == 0) {
                            // Account Details
                            Text(
                                text = "Account Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailRow("Account Name", card.name, textColor = Color.White)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Account Number",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Show Info",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            showAccountNumber = true
                                            coroutineScope.launch {
                                                delay(3000)
                                                showAccountNumber = false
                                            }
                                        }
                                )
                                if (showAccountNumber) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_file_copy_24),
                                        contentDescription = "Copy Account Number",
                                        tint = Color(0xFF8AAEBD),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Account Number", card.accountNumber)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Account number copied!", Toast.LENGTH_SHORT).show()
                                            }
                                    )
                                }
                            }
                            Text(
                                text = if (showAccountNumber) card.accountNumber else formatAccountNumber(card.accountNumber),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            DetailRow("Balance", "${card.balance} KWD", textColor = Color.White)
                            DetailRow("Type", card.accountType.name, textColor = Color.White)
                            DetailRow("Status", if (card.active) "Active" else "Inactive", textColor = Color.White)
                        } else {
                            // Transactions placeholder
                            Text(
                                text = "Transaction History",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No transactions yet.",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
private fun DetailRow(label: String, value: String, textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun ServiceRoundButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A1D))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        icon()
    }
}

private fun formatAccountNumber(accountNumber: String): String {
    // Mask all but last 4 digits
    return accountNumber.replaceRange(0, accountNumber.length - 4, "*".repeat(accountNumber.length - 4))
}

@Composable
fun ServicesRow(
    onTransfer: () -> Unit,
    onTransferToOthers: () -> Unit,
    onCloseAccount: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
    ) {
        ServiceRoundButton(
            icon = { CardTransferBoldIcon(modifier = Modifier.size(28.dp)) },
            onClick = onTransfer
        )
        ServiceRoundButton(
            icon = { TransferUsersIcon(modifier = Modifier.size(28.dp)) },
            onClick = onTransferToOthers
        )
        ServiceRoundButton(
            icon = { CreditCardCloseIcon(modifier = Modifier.size(28.dp)) },
            onClick = onCloseAccount
        )
    }
} 