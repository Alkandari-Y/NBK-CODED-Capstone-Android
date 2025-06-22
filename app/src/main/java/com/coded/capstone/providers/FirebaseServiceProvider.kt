package com.coded.capstone.providers

import com.coded.capstone.data.requests.firebase.FirebaseTokenRequest
import com.coded.capstone.data.responses.firebase.FirebaseTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for Firebase device registration.
 */
interface FirebaseServiceProvider {

    /**
     * Registers a device with the backend by sending its Firebase token.
     *
     * @param request The request containing the user, device, and token information.
     * @return A Response containing the result of the registration attempt.
     */
    @POST("/api/v1/firebase/register")
    suspend fun registerDevice(
        @Body request: FirebaseTokenRequest
    ): Response<FirebaseTokenResponse>

    /**
     * Updates an existing Firebase token on the backend.
     * This might be used if the token is refreshed.
     *
     * @param request The request containing the new token information.
     * @return A Response containing the result of the update attempt.
     */
    @POST("/api/v1/firebase/update-token")
    suspend fun updateFirebaseToken(
        @Body request: FirebaseTokenRequest
    ): Response<FirebaseTokenResponse>
} 