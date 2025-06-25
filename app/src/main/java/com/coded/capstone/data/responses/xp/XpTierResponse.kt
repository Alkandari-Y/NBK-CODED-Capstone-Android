package com.coded.capstone.data.responses.xp

data class XpTierResponse(
    val id: Long,
    val minXp: Long,
    val maxXp: Long,
    val name: String,
    val xpPerkMultiplier: Double,
    val xpPerNotification: Long,
    val xpPerPromotion: Long,
    val perkAmountPercentage: Long
)