package com.coded.capstone.services

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.coded.capstone.R
import com.coded.capstone.data.requests.ble.BlueToothBeaconNotificationRequest
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.providers.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BleScanService : Service() {

    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanJob: Job? = null
    val scope = CoroutineScope(Dispatchers.IO)

    private val BEACON_ID_PATTERN = Regex("""^nbk-(\d+)$""")
    private val lastSentMap = mutableMapOf<Long, Long>()

    private val debounceIntervalMillis = 30_000L
    private val scanIntervalMillis = 20_000L
    private val scanDurationMillis = 500L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BLEScan", "BleScanService started")

        if (!isBleReady()) {
            Log.e("BLEScan", "Bluetooth not ready.")
            stopSelf()
            return START_NOT_STICKY
        }

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothLeScanner = bluetoothManager.adapter.bluetoothLeScanner

        startForeground(1, createNotification())
        startScan()

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "ble_scan_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "BLE Scan",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Rewards Scanning")
            .setContentText("Searching for nearby Klue partners")
//            .setSmallIcon(R.drawable.klue)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.klue))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    private fun startScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {
            Log.e("BLEScan", "BLE permissions missing.")
            stopSelf()
            return
        }

        val scanMode = if (isAppInForeground()) {
            ScanSettings.SCAN_MODE_LOW_LATENCY
        } else {
            ScanSettings.SCAN_MODE_BALANCED
        }
        val settings = ScanSettings.Builder()
            .setScanMode(scanMode)
            .build()

        scanJob = scope.launch {
            while (this.isActive) {
                if (!isBleReady()) {
                    Log.e("BLEScan", "Bluetooth turned off during scan, stopping service.")
                    stopSelf()
                    break
                }

                Log.d("BLEScan", "Starting scan cycle...")
                bluetoothLeScanner.startScan(null, settings, scanCallback)

                delay(scanDurationMillis)

                Log.d("BLEScan", "Stopping scan cycle...")
                bluetoothLeScanner.stopScan(scanCallback)

                delay(scanIntervalMillis - scanDurationMillis)
            }
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord ?: return
            val deviceName = scanRecord.deviceName ?: result.device.name
            Log.d("BLEScan", "Found: ${deviceName ?: "Unknown"} RSSI=${result.rssi}")

            if (deviceName != null && deviceName.startsWith("nbk-")) {
                Log.i("BLEScan", "MATCHED nbk- device by name!")
                if (deviceName != null && deviceName.startsWith("nbk-")) {
                    processMatchedBeacon(deviceName)
                    return
                }
            }

            val feaaUuid = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")
            val serviceData = scanRecord.getServiceData(feaaUuid)

            if (serviceData != null) {
                val hex = serviceData.joinToString("") { "%02x".format(it) }
                Log.d("BLEScan", "FEAA Service Data: $hex")
                Log.i("BLEScan", "MATCHED nbk- beacon from FEAA service data!")
                if (hex.contains("6e626b2d")) {
                    processMatchedBeacon(deviceName)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            val errorMsg = when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "Scan already started"
                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "App registration failed"
                ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "Internal error"
                ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "Feature unsupported"
                else -> "Unknown error"
            }
            Log.e("BLEScan", "Scan failed: $errorMsg ($errorCode)")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onDestroy() {
        super.onDestroy()
        scanJob?.cancel()
        bluetoothLeScanner.stopScan(scanCallback)
        Log.d("BLEScan", "BLE scan service stopped.")
    }

    private fun isBleReady(): Boolean {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        return adapter != null && adapter.isEnabled
    }

    private fun extractBeaconId(beaconName: String): Long? {
        val match = BEACON_ID_PATTERN.matchEntire(beaconName) ?: return null
        return match.groupValues[1].toLongOrNull()
    }


    private fun processMatchedBeacon(deviceName: String?) {
        extractBeaconId(deviceName ?: return)?.let { beaconId ->
            if (shouldSend(beaconId)) {
                sendToMicroservice(beaconId)
            }
        }
    }

    private fun sendToMicroservice(deviceId: Long) {
        scope.launch {
            try {
                val api = RetrofitInstance.getRecommendationServiceProvide(applicationContext)
                val response = api.sendBleDevice(
                    BlueToothBeaconNotificationRequest(
                        beaconId =  deviceId,
                        userId = TokenManager.getUserIdFromSharedPref(applicationContext)
                    )
                )
                Log.d("Microservice", "Sent BLE device: $deviceId - Response code: ${response.code()}")
            } catch (e: Exception) {
                Log.e("Microservice", "Error: ${e.message}")
            }
        }
    }

    private fun shouldSend(deviceId: Long): Boolean {
        cleanOldEntries()
        val now = System.currentTimeMillis()
        val lastSent = lastSentMap[deviceId] ?: 0
        return if (now - lastSent > debounceIntervalMillis) {
            lastSentMap[deviceId] = now
            true
        } else {
            Log.d("BLEScan", "Skipping send for $deviceId — last sent ${now - lastSent}ms ago")
            false
        }
    }

    private fun cleanOldEntries() {
        val now = System.currentTimeMillis()
        lastSentMap.entries.removeIf { now - it.value > 60_000 * 5 } // purge after 5 minutes
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        val packageName = applicationContext.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    override fun onBind(intent: Intent?): IBinder? = null


}
