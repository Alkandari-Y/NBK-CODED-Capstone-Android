package com.coded.capstone.data.responses.account


import java.math.BigDecimal



data class AccountProduct(
    val id: Long?,
    val name: String?,
    val accountType: String?,
    val interestRate: Double?,
    val minBalanceRequired: Double?,
    val creditLimit: Double?,
    val annualFee: Double?,
    val minSalary: Double?,
    val image: String?,
)



data class AccountResponse(
    val id: Long,
    val accountNumber: String?,
    val balance: BigDecimal,
    val ownerId: Long,
    val ownerType: String?,
    val accountProductId: Long?,
    val accountType: String?
)

