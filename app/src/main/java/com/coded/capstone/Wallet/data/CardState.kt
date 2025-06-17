package com.coded.capstone.Wallet.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.coded.capstone.data.responses.account.AccountResponse

data class CardState(
    val card: AccountResponse,
    val isFlipped: MutableState<Boolean> = mutableStateOf(false),
    val isSelected: MutableState<Boolean> = mutableStateOf(false)
)

data class PaymentCard(
    val id: Long,
    val accountNumber: String,
    val name: String,
    val balance: String,
    val accountType: String,
    val isActive: Boolean
) 