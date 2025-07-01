package com.coded.capstone.screens.payment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.MainActivity
import java.math.BigDecimal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

@Composable
fun NfcPaymentScreen(
    navController: NavController,
    amount: BigDecimal,
    sourceAccountNumber: String
) {
    val context = LocalContext.current
    val mainActivity = context as MainActivity

    var paymentStatus by remember { mutableStateOf("Ready to pay") }
    var isNfcActive by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var paymentResult by remember { mutableStateOf<String?>(null) }
    var showSuccessToast by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var showNfcErrorDialog by remember { mutableStateOf(false) }
    var nfcErrorMessage by remember { mutableStateOf("") }

    // Set up NFC callback
    LaunchedEffect(Unit) {
        mainActivity.setSourceAccountNumber(sourceAccountNumber)
        mainActivity.setNfcPaymentCallback(object : MainActivity.NfcPaymentCallback {
            override fun onPaymentStarted() {
                Log.d("NfcPaymentScreen", "Payment started callback received")
                paymentStatus = "Payment started..."
            }

            override fun onPaymentSuccess(transactionId: String) {
                Log.d("NfcPaymentScreen", "Payment success callback received: $transactionId")
                paymentStatus = "Payment completed successfully!"
                // Remove success toast - will be handled in WalletScreen
                navController.popBackStack()
            }

            override fun onPaymentFailed(error: String) {
                Log.d("NfcPaymentScreen", "Payment failed callback received: $error")
                paymentStatus = "Payment failed"
                nfcErrorMessage = error
                showNfcErrorDialog = true
            }

            override fun onNfcNotAvailable() {
                Log.d("NfcPaymentScreen", "NFC not available callback received")
                paymentStatus = "NFC not available"
                nfcErrorMessage = "NFC is not available on this device"
                showNfcErrorDialog = true
            }

            override fun onNfcNotEnabled() {
                Log.d("NfcPaymentScreen", "NFC not enabled callback received")
                paymentStatus = "NFC not enabled"
                nfcErrorMessage = "Please enable NFC in your device settings"
                showNfcErrorDialog = true
            }

            override fun onCardDataRead(destinationAccount: String, amount: BigDecimal) {
                Log.d("NfcPaymentScreen", "Card data read callback received: $destinationAccount, $amount")
                paymentStatus = "Card detected: $${amount} to $destinationAccount"
            }
        })
    }

    // Add a timeout mechanism to show results even if callbacks don't fire
    LaunchedEffect(isNfcActive) {
        if (isNfcActive) {
            delay(10000) // 10 second timeout
            if (isNfcActive && !showSuccessToast && !showNfcErrorDialog) {
                Log.d("NfcPaymentScreen", "Payment timeout - showing timeout dialog")
                paymentStatus = "Payment timeout"
                nfcErrorMessage = "No NFC card detected within 10 seconds. Please try again."
                showNfcErrorDialog = true
                isNfcActive = false
            }
        }
    }

    // Clean up NFC when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            mainActivity.stopNfcPayment()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NFC Payment",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Payment Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Amount: $${amount}")
                Text("From Account: $sourceAccountNumber")
                Text("Status: $paymentStatus")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Pay Button - Only show when NFC is not active
        if (!isNfcActive) {
            Button(
                onClick = {
                    if (mainActivity.isNfcAvailable() && mainActivity.isNfcEnabled()) {
                        mainActivity.startNfcPayment()
                        isNfcActive = true
                        paymentStatus = "Hold your phone near the NFC card to make payment"
                        successMessage = "Hold your phone near the NFC card to make payment"
                        showSuccessToast = true

                        // Auto-hide instruction after 2 seconds
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            delay(2000)
                            showSuccessToast = false
                        }
                    } else {
                        if (!mainActivity.isNfcAvailable()) {
                            nfcErrorMessage = "NFC is not available on this device"
                        } else {
                            nfcErrorMessage = "Please enable NFC in your device settings"
                        }
                        showNfcErrorDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Pay with NFC", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            // Test button to verify callbacks work
            Button(
                onClick = {
                    Log.d("NfcPaymentScreen", "Test button clicked - simulating payment failure")
                    paymentStatus = "Test: Payment failed"
                    nfcErrorMessage = "This is a test error message"
                    showNfcErrorDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Test UI (Simulate Error)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            // Cancel Button - Show when NFC is active
            Button(
                onClick = {
                    mainActivity.stopNfcPayment()
                    isNfcActive = false
                    paymentStatus = "Payment cancelled"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cancel Payment", fontSize = 18.sp)
            }
        }
    }

    // Success Toast (matching your existing style)
    if (showSuccessToast) {
        AnimatedVisibility(
            visible = showSuccessToast,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF374151) // Dark gray background instead of green
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = "NFC",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = successMessage,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // NFC Error Dialog (matching your existing style)
    if (showNfcErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showNfcErrorDialog = false
                nfcErrorMessage = ""
                isNfcActive = false
                paymentStatus = "Ready to pay"
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Payment Error",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(nfcErrorMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNfcErrorDialog = false
                        nfcErrorMessage = ""
                        isNfcActive = false
                        paymentStatus = "Ready to pay"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("OK", fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}