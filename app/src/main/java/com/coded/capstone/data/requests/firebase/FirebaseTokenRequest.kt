package com.coded.capstone.data.requests.firebase

/**
 * Data class for the request to register a device's Firebase token with the backend.
 *
 * @property userId The unique ID of the user.
 * @property deviceId The unique ID of the device.
 * @property jwtToken The user's current JWT for authentication.
 * @property firebaseToken The Firebase Cloud Messaging token for this device.
 * @property deviceType The type of the device (e.g., "android").
 * @property appVersion The version of the application.
 */
data class FirebaseTokenRequest(
    val userId: Long,
    val deviceId: String,
    val jwtToken: String,
    val firebaseToken: String? = null,
    val deviceType: String = "android",
    val appVersion: String = "1.0.0"
) 