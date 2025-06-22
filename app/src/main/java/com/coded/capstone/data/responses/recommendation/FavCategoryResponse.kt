package com.coded.capstone.data.responses.recommendation

data class FavCategoryResponse(
    val favCategories: List<FavCategoryDto>
)

data class FavCategoryDto(
    val id: Long,
    val categoryId:Long,
    val createAt: String
)