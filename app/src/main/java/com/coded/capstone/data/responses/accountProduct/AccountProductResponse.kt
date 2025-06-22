package com.coded.capstone.data.responses.accountProduct

import com.coded.capstone.data.responses.perk.PerkDto

data class AccountProductResponse(
    val id: Long?,
    val name: String?,
    val accountType: String?,
    val interestRate: Double?,
    val minBalanceRequired: Double?,
    val creditLimit: Double?,
    val annualFee: Double?,
    val minSalary: Double?,
    val image: String?,
    val perks: List<PerkDto>?,
    val categoryIds: List<Long>?,
    val categoryNames: List<String>?
)