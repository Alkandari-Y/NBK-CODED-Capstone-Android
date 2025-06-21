package com.coded.capstone.data.responses.perk

import com.coded.capstone.data.responses.category.CategoryDto

data class PerkDto(
    val id: Long?,
    val type: String?,
    val isTierBased: Boolean?,
    val rewardsXp: Int?,
    val perkAmount: Double?,
    val minPayment: Double?,
    val accountProductId: Long?,
    val categories: List<CategoryDto>?
)