package com.coded.capstone.providers

import com.coded.capstone.Services.FirebaseToken
import com.coded.capstone.data.requests.kyc.KYCRequest
import com.coded.capstone.data.responses.kyc.KYCResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationServiceProvider {
    @POST("/api/v1/here/test")
    suspend fun testToken(@Body request: FirebaseToken): Response<String>
}