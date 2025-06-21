package com.coded.capstone.data.responses.account

import java.math.BigDecimal

data class TransactionDetails(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val createdAt: String,
    val category: String
)
