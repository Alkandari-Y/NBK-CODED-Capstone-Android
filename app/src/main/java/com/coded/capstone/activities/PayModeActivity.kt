package com.coded.capstone.activities

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NdefMessage
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class PayModeActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var isNfcSupported = true
    private var countdownSeconds by mutableStateOf(59)
    private var isWaitingForNfc by mutableStateOf(true)
    private var nfcStatus by mutableStateOf("Hold your device near an NFC tag")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupNfc()
        startCountdown()
        
        setContent {
            PayModeScreen()
        }
    }

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        when {
            nfcAdapter == null -> {
                Log.e("PayModeActivity", "NFC not supported on this device")
                isNfcSupported = false
                nfcStatus = "NFC not supported on this device"
            }
            nfcAdapter?.isEnabled != true -> {
                Log.e("PayModeActivity", "NFC is disabled")
                nfcStatus = "NFC is disabled. Please enable NFC in settings."
            }
            else -> {
                Log.d("PayModeActivity", "NFC is ready")
                nfcStatus = "Hold your device near an NFC tag"
            }
        }
    }

    private fun startCountdown() {
        // Start countdown in a coroutine
        lifecycleScope.launch {
            while (countdownSeconds > 0 && isWaitingForNfc) {
                delay(1000)
                countdownSeconds--
            }
            if (countdownSeconds <= 0) {
                // Timeout - finish activity
                finishWithResult(RESULT_CANCELED, "Payment timeout")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    private fun enableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            try {
                val intent = Intent(this, javaClass).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                val pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, 
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                
                val intentFilters = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )
                
                val techLists = arrayOf(
                    arrayOf("android.nfc.tech.NfcF"),
                    arrayOf("android.nfc.tech.NfcA"),
                    arrayOf("android.nfc.tech.NfcB"),
                    arrayOf("android.nfc.tech.NfcV"),
                    arrayOf("android.nfc.tech.Ndef"),
                    arrayOf("android.nfc.tech.NdefFormatable")
                )
                
                adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
                Log.d("PayModeActivity", "NFC foreground dispatch enabled")
            } catch (e: Exception) {
                Log.e("PayModeActivity", "Error enabling NFC foreground dispatch: ${e.message}")
            }
        }
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            try {
                adapter.disableForegroundDispatch(this)
                Log.d("PayModeActivity", "NFC foreground dispatch disabled")
            } catch (e: Exception) {
                Log.e("PayModeActivity", "Error disabling NFC foreground dispatch: ${e.message}")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("PayModeActivity", "New intent received: ${intent.action}")
        
        when (intent.action) {
            NfcAdapter.ACTION_NDEF_DISCOVERED,
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED -> {
                handleNfcIntent(intent)
            }
        }
    }

    private fun handleNfcIntent(intent: Intent) {
        try {
            nfcStatus = "NFC tag detected! Processing..."
            isWaitingForNfc = false
            
            // Get the tag from the intent
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.d("PayModeActivity", "NFC tag detected: $tag")
            
            // Try to read NDEF messages
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null && rawMsgs.isNotEmpty()) {
                val msgs = rawMsgs.map { it as NdefMessage }
                for (msg in msgs) {
                    for (record in msg.records) {
                        val payload = record.payload
                        val payloadText = String(payload, Charsets.UTF_8)
                        Log.d("PayModeActivity", "NDEF payload: $payloadText")
                        
                        // Process the payment
                        processPayment(payloadText)
                        return
                    }
                }
            } else {
                // No NDEF data, create a mock payment for testing
                Log.d("PayModeActivity", "No NDEF data found, creating mock payment")
                val mockPaymentData = createMockPaymentData()
                processPayment(mockPaymentData)
            }
            
        } catch (e: Exception) {
            Log.e("PayModeActivity", "Error handling NFC intent: ${e.message}")
            nfcStatus = "Error reading NFC tag"
            finishWithResult(RESULT_CANCELED, "NFC read error: ${e.message}")
        }
    }

    private fun createMockPaymentData(): String {
        // Create mock payment data for testing
        val mockData = JSONObject().apply {
            put("destinationAccountNumber", "1234567890")
            put("amount", "10.00")
            put("merchantName", "Test Merchant")
        }
        return mockData.toString()
    }

    private fun processPayment(payloadText: String) {
        try {
            nfcStatus = "Processing payment..."
            
            // Send the payload back to the calling activity
            val resultIntent = Intent().apply {
                putExtra("nfc_payload", payloadText)
                putExtra("payment_status", "success")
            }
            
            setResult(RESULT_OK, resultIntent)
            finishWithResult(RESULT_OK, "Payment initiated successfully")
            
        } catch (e: Exception) {
            Log.e("PayModeActivity", "Error processing payment: ${e.message}")
            finishWithResult(RESULT_CANCELED, "Payment processing error: ${e.message}")
        }
    }

    private fun finishWithResult(resultCode: Int, message: String) {
        Log.d("PayModeActivity", "Finishing with result: $resultCode, message: $message")
        
        if (resultCode == RESULT_OK) {
            // Send success broadcast
            val broadcastIntent = Intent("com.coded.capstone.NFC_PAYMENT_SUCCESS").apply {
                putExtra("message", message)
            }
            sendBroadcast(broadcastIntent)
        }
        
        finish()
    }

    @Composable
    private fun PayModeScreen() {
        val infiniteTransition = rememberInfiniteTransition(label = "nfc_animation")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "nfc_scale"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { finishWithResult(RESULT_CANCELED, "Payment cancelled by user") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // NFC Icon with animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(
                        Color(0xFF3B82F6).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = "NFC",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Ready for Payment",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status text
            Text(
                text = nfcStatus,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Countdown timer
            if (isWaitingForNfc) {
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B82F6)),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = countdownSeconds.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "seconds remaining",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Cancel button
            OutlinedButton(
                onClick = { finishWithResult(RESULT_CANCELED, "Payment cancelled by user") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}