package com.coded.capstone.wallet.data

import com.coded.capstone.data.enums.AccountType
import java.math.BigDecimal

data class WalletAccountDisplayModel(
    val id: Long,
    val accountNumber: String,
    val maskedAccountNumber: String,
    val balance: BigDecimal,
    val formattedBalance: String,
    val accountType: AccountType,
    val creditLimit: BigDecimal?,
    val formattedCreditLimit: String?,
    val holderName: String,
    val isActive: Boolean,
    val canTopUp: Boolean,
    val canTransfer: Boolean,
    val accountProductName: String,
    val perks: List<AccountPerkDisplayModel>
)

data class AccountPerkDisplayModel(
    val id: Long,
    val type: PerkType,
    val title: String,
    val description: String,
    val value: String,
    val minPayment: BigDecimal,
    val rewardsXp: Long,
    val perkAmount: BigDecimal,
    val isTierBased: Boolean
)

enum class PerkType {
    CASHBACK,
    REWARDS,
    INSURANCE,
    FEE_WAIVER
}