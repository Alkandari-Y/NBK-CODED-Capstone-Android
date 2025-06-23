package com.coded.capstone.data.responses.recommendation

import com.coded.capstone.data.responses.category.CategoryDto

data class BusinessPartnerResponse(
    val id: Long,
    val name: String,
    val logoUrl: String,
    val category: CategoryDto,
)