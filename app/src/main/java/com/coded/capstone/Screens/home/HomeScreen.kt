package com.coded.capstone.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.coded.capstone.data.enums.AccountType
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.viewModels.AuthViewModel
import java.math.BigDecimal

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

    var totalBalance by remember { mutableStateOf(BigDecimal.ZERO) }
    // Calculate total balance
    LaunchedEffect(sampleAccounts) {
        totalBalance = sampleAccounts.sumOf { it.balance }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Total Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${totalBalance} KWD",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }
} 