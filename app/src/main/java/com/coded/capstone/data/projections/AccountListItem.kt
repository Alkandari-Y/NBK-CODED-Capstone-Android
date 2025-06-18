package com.coded.capstone.data.projections

import java.math.BigDecimal

data class AccountListItem(
    val id: Long,
    val name: String,
    val accountNumber: String,
    val balance: BigDecimal,
    val active: Boolean,
    val ownerId: Long,
    val isPrimary: Boolean
) 