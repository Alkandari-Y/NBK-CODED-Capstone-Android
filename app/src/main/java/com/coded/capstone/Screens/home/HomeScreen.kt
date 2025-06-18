package com.coded.capstone.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.data.enums.AccountType
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.viewModels.AuthViewModel
import java.math.BigDecimal
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Sample data - replace with actual API calls
    val sampleAccounts = remember {
        listOf(
            AccountResponse(
                accountNumber = "1234567890123456",
                id = 1L,
                balance = BigDecimal("8247.50"),
                name = "NBK Classic",
                active = true,
                ownerId = 1L,
                accountType = AccountType.DEBIT
            ),
            AccountResponse(
                accountNumber = "9876543210985678",
                id = 2L,
                balance = BigDecimal("4600.00"),
                name = "Savings Account",
                active = true,
                ownerId = 1L,
                accountType = AccountType.CREDIT
            )
        )
    }

    var totalBalance by remember { mutableStateOf(BigDecimal.ZERO) }
    val userName = "Sarah" // This should come from user data

    // Calculate total balance
    LaunchedEffect(sampleAccounts) {
        totalBalance = sampleAccounts.sumOf { it.balance }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        IconButton(onClick = { /* Open drawer */ }) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color(0xFF666666)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
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


            item {
                // Reward Card Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2C3E50)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "G",
                                            color = Color(0xFF2C3E50),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        "Gold Tier",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Reward Balance",
                                    color = Color(0xFFBDC3C7),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    "KD 127.80",
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "1,250 XP Points",
                                    color = Color(0xFFBDC3C7),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "250 XP to Platinum",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Button(
                                onClick = { /* Redeem */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF34495E)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Redeem", color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                // My Accounts Section
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
                        onClick = { navController.navigate("accounts") }
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

            items(sampleAccounts) { account ->
                AccountCard(account = account)
            }





            item {
                // Bottom Navigation Space
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun AccountCard(account: AccountResponse) {
    val gradient = if (account.accountType == AccountType.DEBIT) {
        Brush.linearGradient(listOf(Color(0xFF1976D2), Color(0xFF64B5F6)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF8E24AA), Color(0xFFCE93D8)))
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (account.accountType == AccountType.DEBIT)
                                Icons.Default.CreditCard
                            else
                                Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = account.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (account.active) {
                                Surface(
                                    color = Color(0xFF43A047),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(22.dp)
                                ) {
                                    Text(
                                        text = "Active",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "****" + account.accountNumber.takeLast(4),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = account.accountType.name.capitalize(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "KD ${String.format("%.2f", account.balance)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (account.accountType == AccountType.CREDIT) {
                        Text(
                            text = "+2.5% APY",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB2FF59)
                        )
                    } else {
                        Text(
                            text = "Available",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}