package com.coded.capstone.data.requests.transaction

import java.math.BigDecimal

data class PaymentCreateRequest(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val type: String = "PAYMENT"
)