package com.coded.capstone.providers

import com.coded.capstone.Screens.Wallet.PaymentDetails
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.kyc.KYCRequest
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.data.requests.transaction.TransferCreateRequest
import com.coded.capstone.data.responses.account.AccountCreateResponse
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.transaction.TransactionDetails
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.kyc.KYCResponse
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.data.responses.xp.UserXpInfoResponse
import com.coded.capstone.data.responses.xp.XpHistoryDto
import com.coded.capstone.data.responses.xp.XpTierResponse
import com.coded.capstone.services.PaymentCreateRequest
import com.coded.capstone.services.PaymentResponse
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

    // onBoarding Account Create
    @POST("/api/v1/accounts/onboarding/create")
    suspend fun onboardingCreateCard(
        @Body accountCreateRequestDto: AccountCreateRequest,
    ): Response<AccountCreateResponse>

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
        @Query("accountNumber") accountNumber: String,
    ): Response<List<TransactionDetails>>

    // Transfer endpoint
    @POST("/api/v1/accounts/transfer")
    suspend fun transfer(
        @Body transferRequest: TransferCreateRequest
    ): Response<TransactionDetails>

    // KYC
    @POST("/api/v1/kyc")
    suspend fun updateKyc(@Body request: KYCRequest): KYCResponse

    @GET("/api/v1/kyc")
    suspend fun getUserKyc(): Response<KYCResponse>

    // Partners
    @GET("/api/v1/partners")
    suspend fun getBusinessPartners(): Response<List<PartnerDto>>


    // XP Tier
    @GET("/api/v1/xp/tiers")
    suspend fun getAllXpTiers(): Response<List<XpTierResponse>>

    @GET("/api/v1/xp/tiers/{tierId}")
    suspend fun getXpTierById(@Path("tierId") tierId: String,): Response<XpTierResponse>

    @GET("/api/v1/xp")
    suspend fun getUserXpInfo(): Response<UserXpInfoResponse>

    @GET("/api/v1/xp/history")
    suspend fun getUserXpHistory(): Response<List<XpHistoryDto>>

    @POST("/api/v1/accounts/purchase")
    suspend fun makePurchase(@Body request: PaymentCreateRequest): Response<PaymentDetails>

}
