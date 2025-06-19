package com.coded.capstone.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.formstates.accounts.AccountCreateForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AccountViewModel(
    private val context: Context
) : ViewModel() {
    sealed class AccountCreateUiState {
        data object Idle : AccountCreateUiState()
        data object Loading : AccountCreateUiState()
        data class Success(val account: AccountResponse) : AccountCreateUiState()
        data class Error(val message: String) : AccountCreateUiState()
    }

    private val _formState = MutableStateFlow(AccountCreateForm())
    val formState: StateFlow<AccountCreateForm> = _formState

    private val _accountUiState = MutableStateFlow<AccountCreateUiState>(
       AccountCreateUiState.Idle)
    val accountUiState: StateFlow<AccountCreateUiState> = _accountUiState

    private val _shouldNavigate = MutableStateFlow(false)
    val shouldNavigate: StateFlow<Boolean> = _shouldNavigate

    fun resetNavigationFlag() {
        _shouldNavigate.value = false
    }

}

