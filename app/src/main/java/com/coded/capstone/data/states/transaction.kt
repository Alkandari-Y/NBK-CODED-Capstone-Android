package com.coded.capstone.data.states

import com.coded.capstone.data.responses.transaction.TransactionDetails

sealed class TransferUiState {
    data object Idle : TransferUiState()
    data object Loading : TransferUiState()
    data class Success(val transaction: TransactionDetails) : TransferUiState()
    data class Error(val message: String, val field: String? = null) : TransferUiState()
}

sealed class TopUpUiState {
    data object Idle : TopUpUiState()
    data object Loading : TopUpUiState()
    data class Success(val message: String) : TopUpUiState()
    data class Error(val message: String, val field: String? = null) : TopUpUiState()
}