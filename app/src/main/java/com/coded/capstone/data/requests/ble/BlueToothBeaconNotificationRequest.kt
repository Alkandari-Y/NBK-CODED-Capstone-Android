package com.coded.capstone.data.requests.ble

data class BlueToothBeaconNotificationRequest(
    val beaconId: Long,
    val userId: Long
)