package com.coded.capstone.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.transaction.TransferCreateRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.states.TransferUiState
import com.coded.capstone.data.states.TopUpUiState
import com.coded.capstone.providers.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class TransactionViewModel(private val context: Context) : ViewModel() {

    private val _transferUiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val transferUiState: StateFlow<TransferUiState> = _transferUiState

    private val _topUpUiState = MutableStateFlow<TopUpUiState>(TopUpUiState.Idle)
    val topUpUiState: StateFlow<TopUpUiState> = _topUpUiState

    fun resetTransferState() {
        _transferUiState.value = TransferUiState.Idle
    }

    fun resetTopUpState() {
        _topUpUiState.value = TopUpUiState.Idle
    }

    fun transfer(
        sourceAccount: AccountResponse,
        destinationAccount: AccountResponse,
        amount: BigDecimal,
        onTransactionSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _transferUiState.value = TransferUiState.Loading
            try {
                val request = TransferCreateRequest(
                    sourceAccountNumber = sourceAccount.accountNumber ?: "",
                    destinationAccountNumber = destinationAccount.accountNumber ?: "",
                    amount = amount
                )

                val response = RetrofitInstance.getBankingServiceProvide(context).transfer(request)

                if (response.isSuccessful) {
                    response.body()?.let { transaction ->
                        _transferUiState.value = TransferUiState.Success(transaction)
                        onTransactionSuccess()
                    } ?: run {
                        _transferUiState.value = TransferUiState.Error("Transfer failed: Empty response")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid transfer request"
                        403 -> "Transfer not allowed"
                        404 -> "Account not found"
                        else -> "Transfer failed: ${response.code()}"
                    }
                    _transferUiState.value = TransferUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _transferUiState.value = TransferUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun topUp(amount: BigDecimal) {
        viewModelScope.launch {
            _topUpUiState.value = TopUpUiState.Loading
            try {
                // Simulate top-up logic (external funding)
                // In real implementation, this would call a top-up endpoint
                kotlinx.coroutines.delay(1500) // Simulate API call
                _topUpUiState.value = TopUpUiState.Success("Top-up of ${amount} KWD successful")
            } catch (e: Exception) {
                _topUpUiState.value = TopUpUiState.Error("Top-up failed: ${e.message}")
            }
        }
    }



}