package com.coded.capstone.data.responses.promotion

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class PromotionResponse(
    val id: Long,
    val name: String,
    val businessPartnerId: Long,
    val type: RewardType,
    @SerializedName("startDate")
    private val _startDate: String,
    @SerializedName("endDate")
    private val _endDate: String,
    val description: String,
    val storeId: Long?
) {
    val startDate: LocalDate
        get() = LocalDate.parse(_startDate)
    
    val endDate: LocalDate
        get() = LocalDate.parse(_endDate)
}

enum class RewardType {
    CASHBACK, DISCOUNT
}
