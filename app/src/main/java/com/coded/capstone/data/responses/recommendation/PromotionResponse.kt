package com.coded.capstone.data.responses.recommendation

import java.time.LocalDate

enum class RewardType {
    DISCOUNT,
    CASHBACK,
    POINTS,
    FREE_ITEM
}

data class PromotionResponse(
    val id: Long,
    val name: String,
    val businessPartnerId: Long,
    val type: RewardType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String,
    val storeId: Long?,
    val xp: Long
)
