package com.coded.capstone.data.responses.transaction

import com.coded.capstone.data.enums.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse(
    val id: Long,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String,
    val date: LocalDateTime,
    val accountId: Long,
    val recipientName: String? = null,
    val recipientAccountNumber: String? = null,
    val referenceNumber: String? = null
) 