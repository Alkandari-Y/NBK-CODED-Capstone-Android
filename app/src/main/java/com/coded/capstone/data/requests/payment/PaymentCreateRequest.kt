package com.coded.capstone.data.requests.payment

import java.math.BigDecimal

data class PaymentCreateRequest(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal)