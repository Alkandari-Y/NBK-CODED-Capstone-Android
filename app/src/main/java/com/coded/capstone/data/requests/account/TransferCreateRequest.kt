package com.coded.capstone.data.requests.account

import com.coded.capstone.data.enums.TransactionType
import java.math.BigDecimal

data class TransferCreateRequest(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val type: TransactionType? = TransactionType.TRANSFER
)