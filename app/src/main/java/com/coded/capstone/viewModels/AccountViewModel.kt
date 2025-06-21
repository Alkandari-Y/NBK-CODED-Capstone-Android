package com.coded.capstone.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.responses.account.AccountCreateResponse
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.account.TransactionDetails
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val context: Context
) : ViewModel() {
    sealed class AccountCreateUiState {
        data object Idle : AccountCreateUiState()
        data object Loading : AccountCreateUiState()
        data class Success(val account: AccountCreateResponse) : AccountCreateUiState()
        data class Error(val message: String) : AccountCreateUiState()
    }


    private val _accountUiState = MutableStateFlow<AccountCreateUiState>(
       AccountCreateUiState.Idle)
    val accountUiState: StateFlow<AccountCreateUiState> = _accountUiState

    private val _shouldNavigate = MutableStateFlow(false)
    val shouldNavigate: StateFlow<Boolean> = _shouldNavigate

    private val _accountTransactions = MutableStateFlow<List<TransactionDetails>?>(null)
    val accountTransactions: StateFlow<List<TransactionDetails>?> = _accountTransactions

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

    fun fetchAccountTransactions(accountNumber: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context)
                    .getAllTransactionsByAccountNumber(accountNumber)

                if (response.isSuccessful) {
                    _accountTransactions.value = response.body()
                } else {
                    _accountTransactions.value = emptyList()
                }
            } catch (e: Exception) {
                _accountTransactions.value = emptyList()
            }
        }
    }

}

