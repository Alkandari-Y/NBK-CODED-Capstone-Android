package com.coded.capstone.data.responses.account


import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import java.math.BigDecimal


data class AccountResponse(
    val id: Long,
    val accountNumber: String?,
    val balance: BigDecimal,
    val ownerId: Long,
    val ownerType: String?,
    val accountProductId: Long?,
    val accountType: String?
)


data class AccountCreateResponse(
    val id: Long,
    val accountNumber: String?,
    val balance: BigDecimal,
    val ownerId: Long,
    val ownerType: String?,
    val accountProduct: AccountProductResponse,
    val accountType: String?
)
