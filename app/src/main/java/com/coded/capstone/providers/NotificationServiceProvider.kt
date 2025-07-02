package com.coded.capstone.providers

import com.coded.capstone.data.requests.notifications.TestFirebaseTokenRequest
import com.coded.capstone.data.responses.notification.NotificationResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationServiceProvider {

    @POST("/api/v1/device/register")
    suspend fun registerFirebaseToken(
        @Body request: TestFirebaseTokenRequest
    ): Response<ResponseBody>


    @POST("/api/v1/device/register")
    suspend fun registerFirebaseToken(
        @Body request: UserDeviceFBTokenRequest
    ): Response<ResponseBody>


    @GET("/api/v1/notifications")
    suspend fun getAllNotifications(): Response<List<NotificationResponseDto>>

}

data class UserDeviceFBTokenRequest(
    val firebaseToken: String
)

