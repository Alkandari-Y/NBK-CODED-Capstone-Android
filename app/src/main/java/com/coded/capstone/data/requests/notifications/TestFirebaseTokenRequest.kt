package com.coded.capstone.data.requests.notifications

import com.google.gson.annotations.SerializedName

/**
 * Data class for sending a test FCM token to the backend.
 * This corresponds to the FirebaseToken data class in the backend's HelloController.
 */
data class TestFirebaseTokenRequest(
    @SerializedName("token")
    val token: String
) 