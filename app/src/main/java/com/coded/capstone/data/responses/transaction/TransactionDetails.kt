package com.coded.capstone.data.responses.transaction

import java.math.BigDecimal

data class TransactionDetails(
    val transactionId: Long,
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val createdAt: String,
    val category: String
)
