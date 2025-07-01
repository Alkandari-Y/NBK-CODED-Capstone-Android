package com.coded.capstone

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.coded.capstone.MapAndGeofencing.LocationPermissionHandler
import com.coded.capstone.MapAndGeofencing.GeofenceManager
import com.coded.capstone.navigation.AppHost
import com.coded.capstone.ui.theme.CapstoneTheme
import com.coded.capstone.managers.NFCManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissionRequestCode = 101

    // NFC Components
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // NFC Setup
        setupNfcComponents()

        requestFirebaseNotificationPermission()
        if (!hasBluetoothPermissions(this)) {
            ActivityCompat.requestPermissions(this, getBluetoothPermissions(), permissionRequestCode)
        }
        enableEdgeToEdge()

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
        // NFC Intent Handling
        handleNfcIntent(intent)
    }
    
    /**
     * Handle deep links when app is already running
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
        handleNfcIntent(intent)
    }
    
    /**
     * Process deep link intent
     */
    private fun handleDeepLink(intent: Intent) {
    }

    // NFC Methods
    private fun setupNfcComponents() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_LONG).show()
            return
        }

        if (nfcAdapter?.isEnabled == false) {
            Toast.makeText(this, "NFC is disabled. Please enable it in Settings", Toast.LENGTH_LONG).show()
        }

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        val ndefFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("application/json")
                addDataType("text/plain")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                Log.e(TAG, "Failed to add MIME type", e)
            }
        }

        intentFilters = arrayOf(
            ndefFilter,
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
    }

    private fun handleNfcIntent(intent: Intent) {
        when (intent.action) {
            NfcAdapter.ACTION_NDEF_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                tag?.let {
                    Log.d(TAG, "NFC Tag detected: ${it}")
                    NFCManager.processNfcTag(it)
                }
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
        try {
            nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable NFC foreground dispatch", e)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disable NFC foreground dispatch", e)
        }
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