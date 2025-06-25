package com.coded.capstone.services

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.coded.capstone.data.requests.ble.BlueToothBeaconNotificationRequest
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.providers.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BleScanService : Service() {

    private lateinit var bluetoothLeScanner: BluetoothLeScanner

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
                channelId, "BLE Scan", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Scanning for Devices")
            .setContentText("BLE scanning is running in the background")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build()
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    private fun startScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("BLEScan", "BLE permissions missing.")
                stopSelf()
                return
            }
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bluetoothLeScanner.startScan(null, settings, scanCallback)
        Log.d("BLEScan", "BLE scan started.")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord ?: return
            val deviceName = scanRecord.deviceName ?: result.device.name
            Log.d("BLEScan", "Found: ${deviceName ?: "Unknown"} RSSI=${result.rssi}")

            if (deviceName != null && deviceName.startsWith("nbk-")) {
                Log.i("BLEScan", "MATCHED nbk- device by name!")
                sendToMicroservice(deviceName)
                return
            }

            val feaaUuid = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")
            val serviceData = scanRecord.getServiceData(feaaUuid)

            if (serviceData != null) {
                val hex = serviceData.joinToString("") { "%02x".format(it) }
                Log.d("BLEScan", "FEAA Service Data: $hex")

                if (hex.contains("6e626b2d")) {
                    Log.i("BLEScan", "MATCHED nbk- beacon from FEAA service data!")
                    sendToMicroservice(deviceName ?: "nbk-detected")
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

    private fun isBleReady(): Boolean {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        return adapter != null && adapter.isEnabled
    }

    private fun sendToMicroservice(deviceName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitInstance.getRecommendationServiceProvide(applicationContext)
                val response = api.sendBleDevice(
                    BlueToothBeaconNotificationRequest(
                        deviceName,
                        userId = TokenManager.getUserIdFromSharedPref(applicationContext)
                    )
                )
                Log.d("Microservice", "Sent BLE device: $deviceName - Response code: ${response.code()}")
            } catch (e: Exception) {
                Log.e("Microservice", "Error: ${e.message}")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
