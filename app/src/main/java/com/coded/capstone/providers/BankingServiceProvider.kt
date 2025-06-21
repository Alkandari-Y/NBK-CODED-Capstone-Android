package com.coded.capstone.providers

import com.coded.capstone.data.requests.account.TransferCreateRequest
import com.coded.capstone.data.requests.kyc.KYCRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.kyc.KYCResponse
import com.coded.capstone.data.responses.transaction.TransactionResponse
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

    // WALLET
    @GET("/api/v1/accounts")
    suspend fun getAllAccounts(): List<AccountResponse>

    @POST("/api/v1/accounts/transfer")
    suspend fun transfer(@Body request: TransferCreateRequest): TransactionResponse // CHANGED

}