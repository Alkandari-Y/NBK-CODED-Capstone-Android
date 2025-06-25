package com.coded.capstone.data.requests.ble

data class BlueToothBeaconNotificationRequest(
    val beaconId: String,
    val userId: Long
)