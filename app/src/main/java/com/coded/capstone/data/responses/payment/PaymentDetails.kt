package com.coded.capstone.data.responses.payment

import com.coded.capstone.data.enums.TransactionType
import com.coded.capstone.data.responses.xp.XpHistoryDto
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentDetails(
    val transactionId: Long,
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val category: String,
    var transactionType: TransactionType = TransactionType.PAYMENT,
    val xpHistoryRecord: List<XpHistoryDto>
)