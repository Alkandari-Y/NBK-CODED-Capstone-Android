package com.coded.capstone.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.wallet.data.WalletUiState
import com.coded.capstone.wallet.data.WalletAccountDisplayModel
import com.coded.capstone.wallet.repository.WalletRepository
import com.coded.capstone.wallet.repository.WalletRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class WalletViewModel(
    private val walletRepository: WalletRepository
) : ViewModel() {

    // Secondary constructor for easy instantiation
    constructor(context: android.content.Context) : this(
        WalletRepositoryImpl(context)
    )

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            walletRepository.getWalletAccounts()
                .onSuccess { accounts ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        accounts = accounts,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load accounts"
                    )
                }
        }
    }

    fun selectCard(accountId: Long) {
        _uiState.value = _uiState.value.copy(selectedAccountId = accountId)
    }

    fun deselectCard() {
        _uiState.value = _uiState.value.copy(
            selectedAccountId = null,
            showTopUpDialog = false,
            showTransferDialog = false,
            selectedSourceAccount = null,
            selectedDestinationAccount = null,
            topUpAmount = BigDecimal.ZERO,
            transferAmount = BigDecimal.ZERO,
            transactionError = null
        )
    }

    // Top Up Dialog methods
    fun showTopUpDialog() {
        _uiState.value = _uiState.value.copy(showTopUpDialog = true)
    }

    fun hideTopUpDialog() {
        _uiState.value = _uiState.value.copy(
            showTopUpDialog = false,
            selectedSourceAccount = null,
            topUpAmount = BigDecimal.ZERO,
            transactionError = null
        )
    }

    fun selectSourceAccount(account: WalletAccountDisplayModel) {
        _uiState.value = _uiState.value.copy(selectedSourceAccount = account)
    }

    fun updateTopUpAmount(amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(topUpAmount = amount)
    }

    // Transfer Dialog methods
    fun showTransferDialog() {
        _uiState.value = _uiState.value.copy(showTransferDialog = true)
    }

    fun hideTransferDialog() {
        _uiState.value = _uiState.value.copy(
            showTransferDialog = false,
            selectedDestinationAccount = null,
            transferAmount = BigDecimal.ZERO,
            transactionError = null
        )
    }

    fun selectDestinationAccount(account: WalletAccountDisplayModel) {
        _uiState.value = _uiState.value.copy(selectedDestinationAccount = account)
    }

    fun updateTransferAmount(amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(transferAmount = amount)
    }

    // Transaction Methods
    fun topUpAccount(fromAccountNumber: String, toAccountNumber: String, amount: BigDecimal) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessingTransaction = true,
                transactionError = null
            )

            walletRepository.topUpAccount(fromAccountNumber, toAccountNumber, amount)
                .onSuccess { transactionDetails ->
                    _uiState.value = _uiState.value.copy(
                        isProcessingTransaction = false,
                        showTopUpDialog = false,
                        selectedSourceAccount = null,
                        topUpAmount = BigDecimal.ZERO
                    )

                    // Reload accounts to reflect updated balances
                    loadAccounts()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isProcessingTransaction = false,
                        transactionError = exception.message ?: "Transfer failed"
                    )
                }
        }
    }

    fun transferFunds(fromAccountNumber: String, toAccountNumber: String, amount: BigDecimal) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessingTransaction = true,
                transactionError = null
            )

            walletRepository.transferFunds(fromAccountNumber, toAccountNumber, amount)
                .onSuccess { transactionDetails ->
                    _uiState.value = _uiState.value.copy(
                        isProcessingTransaction = false,
                        showTransferDialog = false,
                        selectedDestinationAccount = null,
                        transferAmount = BigDecimal.ZERO
                    )

                    // Reload accounts to reflect updated balances
                    loadAccounts()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isProcessingTransaction = false,
                        transactionError = exception.message ?: "Transfer failed"
                    )
                }
        }
    }
}