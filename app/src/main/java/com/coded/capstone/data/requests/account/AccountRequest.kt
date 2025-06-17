package com.coded.capstone.data.requests.account

import com.coded.capstone.data.enums.AccountType
import java.math.BigDecimal

data class AccountRequest(
    val id: Long? = null,
    val name: String,
    val balance: BigDecimal,
    val active: Boolean = true,
    val ownerId: Long? = null,
    val accountType: AccountType = AccountType.DEBIT,
    val accountNumber: String = ""
)

