package com.coded.capstone.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BankingApiService {
    @POST("api/v1/accounts/purchase")
    suspend fun makePurchase(@Body request: PaymentCreateRequest): Response<PaymentResponse>
}

data class PaymentCreateRequest(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: java.math.BigDecimal,
    val type: TransactionType = TransactionType.PAYMENT
)

data class PaymentResponse(
    val success: Boolean,
    val transactionId: String?,
    val message: String?
)

enum class TransactionType {
    PAYMENT
} 