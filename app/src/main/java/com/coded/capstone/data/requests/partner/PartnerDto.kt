package com.coded.capstone.data.requests.partner

import com.coded.capstone.data.responses.category.CategoryDto
import java.math.BigDecimal

data class PartnerDto(
    val id: Long?,
    val name: String,
    val logoUrl: String,
    val category: CategoryDto
)
