package com.coded.capstone.data.responses.kyc

import android.R
import java.math.BigDecimal

data class KYCResponse(
    val id: Long,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    var dateOfBirth: String? = null,
    val salary: BigDecimal,
    val nationality: String,
    val civilId: String,
    val mobileNumber: String,
)


