package com.coded.capstone.data.requests.kyc

import java.math.BigDecimal

data class KYCRequest(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val salary: BigDecimal,
    val nationality: String,
   val mobileNumber: String,
   val civilId: String
)

