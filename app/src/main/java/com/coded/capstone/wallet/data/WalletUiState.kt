package com.coded.capstone.wallet.data

import java.math.BigDecimal

data class WalletUiState(
    val isLoading: Boolean = false,
    val accounts: List<WalletAccountDisplayModel> = emptyList(),
    val selectedAccountId: Long? = null,
    val error: String? = null,

    // Transaction dialogs
    val showTopUpDialog: Boolean = false,
    val showTransferDialog: Boolean = false,
    val isProcessingTransaction: Boolean = false,
    val transactionError: String? = null,

    // Transaction form data
    val selectedSourceAccount: WalletAccountDisplayModel? = null,
    val selectedDestinationAccount: WalletAccountDisplayModel? = null,
    val topUpAmount: BigDecimal = BigDecimal.ZERO,
    val transferAmount: BigDecimal = BigDecimal.ZERO
) {
    val selectedAccount: WalletAccountDisplayModel?
        get() = accounts.find { it.id == selectedAccountId }
}