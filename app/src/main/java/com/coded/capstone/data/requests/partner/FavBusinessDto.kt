package com.coded.capstone.data.requests.partner


data class FavBusinessDto(
    val id: Long,
    val userId:Long,
    val partnerId: Long
)

data class FavBusinessResponse(
    val favBusinesses: List<FavBusinessDto>
)