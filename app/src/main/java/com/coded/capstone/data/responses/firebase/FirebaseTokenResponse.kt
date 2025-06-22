package com.coded.capstone.data.responses.firebase

/**
 * Data class for the response from the backend after a Firebase device registration attempt.
 *
 * @property success Indicates whether the operation was successful.
 * @property message A message from the server, e.g., "Device registered successfully".
 * @property firebaseToken The Firebase token that was registered.
 * @property deviceId The device ID that was registered.
 * @property userId The user ID associated with the registration.
 * @property isRegistered A boolean flag confirming the registration status.
 */
data class FirebaseTokenResponse(
    val success: Boolean,
    val message: String,
    val firebaseToken: String? = null,
    val deviceId: String? = null,
    val userId: Long? = null,
    val isRegistered: Boolean = false
) 