package com.coded.capstone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.coded.capstone.MapAndGeofencing.LocationPermissionHandler
import com.coded.capstone.MapAndGeofencing.GeofenceManager
import com.coded.capstone.deeplink.DeepLinkHandler
import com.coded.capstone.navigation.AppHost
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.services.NfcPaymentService
import com.coded.capstone.ui.theme.CapstoneTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissionRequestCode = 101
    private lateinit var nfcPaymentService: NfcPaymentService
    
    // NFC callback interface
    interface NfcPaymentCallback {
        fun onPaymentStarted()
        fun onPaymentSuccess(transactionId: String)
        fun onPaymentFailed(error: String)
        fun onNfcNotAvailable()
        fun onNfcNotEnabled()
        fun onCardDataRead(destinationAccount: String, amount: java.math.BigDecimal)
    }
    
    private var nfcCallback: NfcPaymentCallback? = null
    private var currentSourceAccountNumber: String? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFirebaseNotificationPermission()
        if (!hasBluetoothPermissions(this)) {
            ActivityCompat.requestPermissions(this, getBluetoothPermissions(), permissionRequestCode)
        }
        enableEdgeToEdge()
        
        // Initialize NFC payment service
        nfcPaymentService = NfcPaymentService(this)
        if (!nfcPaymentService.initialize(this)) {
            Log.w("MainActivity", "NFC not available or not enabled")
        }

        setContent {
            CapstoneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MaterialTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            LocationPermissionHandler(
                                onPermissionGranted = {
                                    // Permissions granted, so we start the geofence service directly.
                                    CoroutineScope(Dispatchers.Main).launch {
                                        GeofenceManager.startGeofencing(applicationContext)
                                    }
                                }
                            )
                            AppHost()
                        }
                    }
                }
            }
        }
        
        // Handle deep link if app was launched via deep link
        handleDeepLink(intent)
    }
    
    override fun onResume() {
        super.onResume()
        // Enable NFC foreground dispatch when activity is in foreground
        nfcPaymentService.enableForegroundDispatch(this)
    }
    
    override fun onPause() {
        super.onPause()
        // Disable NFC foreground dispatch when activity is in background
        nfcPaymentService.disableForegroundDispatch(this)
    }
    
    /**
     * Handle NFC tag discovery
     */
    private fun handleNfcTag(tag: Tag) {
        Log.d("MainActivity", "NFC tag discovered: ${tag.id.toHexString()}")
        
        // Check if we have a source account number
        if (currentSourceAccountNumber == null) {
            Log.e("MainActivity", "No source account number set")
            nfcCallback?.onPaymentFailed("No source account selected")
            return
        }
        
        // Process payment with the discovered tag
        CoroutineScope(Dispatchers.Main).launch {
            nfcPaymentService.processPayment(
                tag = tag,
                sourceAccountNumber = currentSourceAccountNumber!!,
                callback = object : NfcPaymentService.NfcPaymentCallback {
                    override fun onPaymentStarted() {
                        Log.d("MainActivity", "Payment started")
                        nfcCallback?.onPaymentStarted()
                    }
                    
                    override fun onPaymentSuccess(transactionId: String) {
                        Log.d("MainActivity", "Payment successful: $transactionId")
                        nfcCallback?.onPaymentSuccess(transactionId)
                    }
                    
                    override fun onPaymentFailed(error: String) {
                        Log.e("MainActivity", "Payment failed: $error")
                        nfcCallback?.onPaymentFailed(error)
                    }
                    
                    override fun onNfcNotAvailable() {
                        Log.w("MainActivity", "NFC not available")
                        nfcCallback?.onNfcNotAvailable()
                    }
                    
                    override fun onNfcNotEnabled() {
                        Log.w("MainActivity", "NFC not enabled")
                        nfcCallback?.onNfcNotEnabled()
                    }
                    
                    override fun onCardDataRead(destinationAccount: String, amount: java.math.BigDecimal) {
                        Log.d("MainActivity", "Card data read: destination=$destinationAccount, amount=$amount")
                        nfcCallback?.onCardDataRead(destinationAccount, amount)
                    }
                }
            )
        }
    }
    
    /**
     * Handle deep links when app is already running
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        
        // Handle NFC intent
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.let { handleNfcTag(it) }
        } else {
            // Handle deep link
            handleDeepLink(intent)
        }
    }
    
    /**
     * Set the source account number for NFC payments
     */
    fun setSourceAccountNumber(accountNumber: String) {
        currentSourceAccountNumber = accountNumber
        Log.d("MainActivity", "Source account number set: $accountNumber")
    }
    
    /**
     * Set NFC payment callback
     */
    fun setNfcPaymentCallback(callback: NfcPaymentCallback) {
        nfcCallback = callback
    }
    
    /**
     * Check if NFC is available
     */
    fun isNfcAvailable(): Boolean {
        return nfcPaymentService.isNfcAvailable()
    }
    
    /**
     * Check if NFC is enabled
     */
    fun isNfcEnabled(): Boolean {
        return nfcPaymentService.isNfcEnabled()
    }
    
    /**
     * Process deep link intent
     */
    private fun handleDeepLink(intent: Intent) {
        // Note: You'll need to pass the NavController from AppHost
        // For now, this is a placeholder that can be connected later
        // DeepLinkHandler.handleDeepLink(intent, navController, this)
    }
    
    /**
     * Convert byte array to hex string
     */
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }

    private fun requestFirebaseNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getBluetoothPermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        return permissions.toTypedArray()
    }

    fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ).all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}

