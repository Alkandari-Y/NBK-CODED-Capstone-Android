package com.coded.capstone.data.responses.recommendation

import com.coded.capstone.data.responses.perk.PerkDto
import java.math.BigDecimal

data class FavCategoryResponse(
    val favCategories: List<FavCategoryDto>
)

data class FavCategoryDto(
    val id: Long,
    val categoryId:Long,
    val createAt: String
)


data class RecommendedAccountProducts(
    val id: Long?,
    val name: String?,
    val description: String?,
    val accountType: String,
    val interestRate: BigDecimal,
    val minBalanceRequired: BigDecimal,
    val creditLimit: BigDecimal,
    val annualFee: BigDecimal,
    val minSalary: BigDecimal,
    val image: String?,
    val perks: List<PerkDto> = emptyList(),
    val categoryIds: Set<Long> = emptySet(),
    val categoryNames: Set<String> = emptySet(),
    val recommended: Boolean = false,
    val isOwned: Boolean = false
)