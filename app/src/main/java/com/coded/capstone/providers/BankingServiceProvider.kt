package com.coded.capstone.providers

import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.kyc.KYCRequest
import com.coded.capstone.data.requests.transaction.TransferCreateRequest
import com.coded.capstone.data.responses.account.AccountCreateResponse
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.transaction.TransactionDetails
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.kyc.KYCResponse
import com.coded.capstone.data.responses.perk.PerkDto
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
    ): Response<AccountCreateResponse>

    @GET("/api/v1/accounts/details")
    suspend fun getAccountDetails(
        @Query("accountId") accountId: String,
    ): Response<AccountResponse>

    // Categories controller
    @GET("/api/v1/categories")
    suspend fun getAllCategories(): Response<List<CategoryDto>>

    // Account Products
    @GET("/api/v1/products")
    suspend fun getAllAccountProducts(): Response<List<AccountProductResponse>>

    @GET("/api/v1/products/{productId}")
    suspend fun getAccountProductDetails(
        @Path("productId") productId: String,
    ): Response<AccountProductResponse>

    @GET("/api/v1/products/{productId}/perks")
    suspend fun getPerksOfAccountProduct(
        @Path("productId") productId: String,
    ): Response<List<PerkDto>>

    // Transactions controller
    @GET("/api/v1/transactions/account")
    suspend fun getAllTransactionsByAccountNumber(
        @Query("accountId") accountId: String,
    ): Response<List<TransactionDetails>>

    // Transfer endpoint
    @POST("/api/v1/transactions/transfer")
    suspend fun transfer(
        @Body transferRequest: TransferCreateRequest
    ): Response<TransactionDetails>

    // KYC
    @POST("/api/v1/kyc")
    suspend fun updateKyc(@Body request: KYCRequest): KYCResponse

    @GET("/api/v1/kyc")
    suspend fun getUserKyc(): Response<KYCResponse>
}