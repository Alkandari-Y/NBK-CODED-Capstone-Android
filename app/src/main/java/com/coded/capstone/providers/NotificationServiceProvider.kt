package com.coded.capstone.providers

import com.coded.capstone.data.requests.notifications.TestFirebaseTokenRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for the backend's notification test controller.
 */
interface NotificationServiceProvider {

    /**
     * Sends the device's FCM token to the backend's test endpoint.
     *
     * @param request The request body containing the FCM token.
     * @return A Response containing a confirmation message from the backend.
     */
    @POST("/api/v1/here/test")
    suspend fun testToken(
        @Body request: TestFirebaseTokenRequest
    ): Response<ResponseBody>
} 