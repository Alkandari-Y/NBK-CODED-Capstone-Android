package com.coded.capstone.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
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
    private val scanResults = mutableListOf<ScanResult>()

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onCreate() {
        super.onCreate()

        if (!isBleReady()) {
            Log.e("BLEScan", "Bluetooth is not ready. Aborting scan.")
            stopSelf()
            return
        }

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothLeScanner = bluetoothManager.adapter.bluetoothLeScanner

        startForeground(1, createNotification())
        startScan()
    }

    private fun createNotification(): Notification {
        val channelId = "ble_scan_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "BLE Scan", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Scanning for Devices")
            .setContentText("BLE scanning is running in the background")
            .build()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun startScan() {
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        // Scan all devices, then manually filter in callback
        bluetoothLeScanner.startScan(null, settings, scanCallback)
        Log.d("BLEScan", "BLE scanning started")
    }


    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord
            val deviceName = result.device.name
            val rssi = result.rssi

            Log.d("BLEScan", "Scan result - RSSI: $rssi, Device: ${deviceName ?: "null"}")

            val serviceData = scanRecord?.serviceData
            val allData = serviceData?.entries?.joinToString { (uuid, data) ->
                "UUID: $uuid, Data: ${data?.joinToString { b -> "%02x".format(b) }}"
            }

            if (!allData.isNullOrEmpty()) {
                Log.d("BLEScan", "Service Data: $allData")

                // Look for "nbk-" in decoded service data
                allData.split(",").forEach {
                    if (it.contains("6e626b2d")) { // hex for "nbk-"
                        Log.i("BLEScan", "MATCHED nbk- beacon from service data!")
                        sendToMicroservice("nbk-detected")
                    }
                }
            }
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
                val response = api.sendBleDevice(BlueToothBeaconNotificationRequest(
                    deviceName,
                    userId = TokenManager.getUserIdFromSharedPref(context = applicationContext)
                ))
                Log.d("Microservice", "Sent BLE device: $deviceName")
            } catch (e: Exception) {
                Log.e("Microservice", "Error: ${e.message}")
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
