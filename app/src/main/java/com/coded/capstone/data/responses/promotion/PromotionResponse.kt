package com.coded.capstone.data.responses.promotion

import java.time.LocalDate

data class PromotionResponse(
    val id: Long,
    val name: String,
    val businessPartnerId: Long,
    val type: RewardType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String,
    val storeId: Long?
)

enum class RewardType {
    CASHBACK, DISCOUNT
}
