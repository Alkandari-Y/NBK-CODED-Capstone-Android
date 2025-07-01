package com.coded.capstone.data.responses.transaction

import java.math.BigDecimal

data class PaymentDetails(
    val transactionId: String?,
    val amount: BigDecimal,
    val sourceAccount: String,
    val destinationAccount: String,
    val status: String,
    val timestamp: String?,
    val message: String?
)