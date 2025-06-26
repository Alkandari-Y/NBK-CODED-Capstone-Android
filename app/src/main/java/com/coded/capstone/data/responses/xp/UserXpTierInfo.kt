package com.coded.capstone.data.responses.xp

data class UserXpInfoResponse(
    val id: Long,
    val userXpAmount: Long,
    val xpTier: XpTierResponse?
)