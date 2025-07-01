package com.coded.capstone.data.responses.xp

data class XpHistoryDto(
    val id: Long,
    val amount: Long,
    val gainMethod: XpGainMethod,
    val transactionId: Long,
    val categoryId: Long,
    val recommendationId: Long? = null,
    val promotionId: Long? = null,
    val xpTierId: Long,
    val userXpId: Long,
    val accountId: Long,
    val accountProductId: Long
)

enum class XpGainMethod {
    NOTIFICATION, PERK, PROMOTION, ONBOARDING
}