package com.coded.capstone.providers

import com.coded.capstone.data.requests.kyc.KYCRequest
import com.coded.capstone.data.responses.kyc.KYCResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BankingServiceProvider {


    // KYC

    @POST("/api/v1/kyc")
    suspend fun updateKyc(@Body request: KYCRequest): KYCResponse

    @GET("/api/v1/kyc")
    suspend fun getUserKyc(): Response<KYCResponse>
}