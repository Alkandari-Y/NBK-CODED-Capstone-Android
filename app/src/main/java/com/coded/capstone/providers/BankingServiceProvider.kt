package com.coded.capstone.providers

import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.kyc.KYCRequest
import com.coded.capstone.data.responses.account.AccountProduct
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.account.TransactionDetails
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.kyc.KYCResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BankingServiceProvider {

    // Accounts Controller

    @GET("/api/v1/accounts")
    suspend fun getAllAccounts(): Response<List<AccountResponse>>

    @POST("/api/v1/accounts")
    suspend fun createAccount(
        @Body accountCreateRequestDto: AccountCreateRequest,
    ): Response<AccountResponse>

    @GET("/api/v1/accounts/details")
    suspend fun getAccountDetails(
        @Query("accountId") accountId: String,
    ): Response<AccountResponse>


    // categories controller
    @GET("/api/v1/categories")
    suspend fun getAllCategories(): Response<List<CategoryDto>>


    // Account Products
    @GET("/api/v1/products")
    suspend fun getAllAccountProducts(): Response<List<AccountProduct>>

    // Transactions controller
    @GET("/api/v1/transactions/account/{accountNumber}")
    suspend fun getAllTransactionsByAccountNumber(
        @Path("accountNumber") accountNumber: String,
    ): Response<List<TransactionDetails>>


    // KYC
    @POST("/api/v1/kyc")
    suspend fun updateKyc(@Body request: KYCRequest): KYCResponse

    @GET("/api/v1/kyc")
    suspend fun getUserKyc(): Response<KYCResponse>
}