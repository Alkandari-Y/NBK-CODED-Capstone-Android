package com.coded.capstone.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.transaction.TransferCreateRequest
import com.coded.capstone.data.responses.account.AccountCreateResponse
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.transaction.TransactionDetails
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class AccountCreateUiState {
    data object Idle : AccountCreateUiState()
    data object Loading : AccountCreateUiState()
    data class Success(val account: AccountCreateResponse) : AccountCreateUiState()
    data class Error(val message: String) : AccountCreateUiState()
}
sealed class TransferUiState {
    data object Idle : TransferUiState()
    data object Loading : TransferUiState()
    data object Success : TransferUiState()
    data class Error(val message: String) : TransferUiState()
}

class AccountViewModel(
    private val context: Context
) : ViewModel() {


    private val _accountUiState = MutableStateFlow<AccountCreateUiState>(
       AccountCreateUiState.Idle)
    val accountUiState: StateFlow<AccountCreateUiState> = _accountUiState

    private val _shouldNavigate = MutableStateFlow(false)
    val shouldNavigate: StateFlow<Boolean> = _shouldNavigate
    private val _transferUiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val transferUiState: StateFlow<TransferUiState> = _transferUiState


    fun resetNavigationFlag() {
        _shouldNavigate.value = false
    }

    fun createAccount(accountProductId:Long) {

        var isValid = true
        _accountUiState.value = AccountCreateUiState.Loading

        viewModelScope.launch {
            try {
                val request = AccountCreateRequest(
                    accountProductId = accountProductId,
                )

                val result = AccountRepository.createAccount(request, context)

                result.onSuccess {
                    _accountUiState.value = AccountCreateUiState.Success(it)
                    _shouldNavigate.value = true
                }.onFailure {
                    _accountUiState.value = AccountCreateUiState.Error(it.message ?: "Unknown error")
                }

            } catch (e: Exception) {
                _accountUiState.value = AccountCreateUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun transferBetweenAccounts(source: String, destination: String, amount: BigDecimal) {
        viewModelScope.launch {
            _transferUiState.value = TransferUiState.Loading
            try {
                val request = TransferCreateRequest(
                    sourceAccountNumber = source,
                    destinationAccountNumber = destination,
                    amount = amount,
                )

                val response = RetrofitInstance.getBankingServiceProvide(context).transfer(request)

                if (response.isSuccessful) {
                    _transferUiState.value = TransferUiState.Success
//                    fetchAccounts()
                } else {
                    _transferUiState.value = TransferUiState.Error("Transfer failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _transferUiState.value = TransferUiState.Error(e.message ?: "Unknown error")
            }
        }
    }


}

