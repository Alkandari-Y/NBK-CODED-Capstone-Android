package com.coded.capstone.data.responses.account

import java.math.BigDecimal
import com.coded.capstone.data.enums.AccountType

data class AccountResponse(
    val accountNumber: String,
    val id: Long,
    val balance: BigDecimal,
    val name: String,
    val active: Boolean,
    val ownerId: Long,
    val accountType: AccountType
) 