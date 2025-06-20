package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountRepository
import com.coded.capstone.respositories.CategoryRepository
import com.coded.capstone.respositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AccountsUiState {
    data object Loading : AccountsUiState()
    data class Success(val accounts: List<AccountResponse>) : AccountsUiState()
    data class Error(val message: String) : AccountsUiState()
}

class HomeScreenViewModel(
    private val context: Context
) : ViewModel() {
    private val _accountsUiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val accountsUiState: StateFlow<AccountsUiState> = _accountsUiState

    private val _selectedAccount = MutableStateFlow<AccountResponse?>(null)
    val selectedAccount: StateFlow<AccountResponse?> = _selectedAccount

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    init {
        viewModelScope.launch {
            if (UserRepository.userInfo == null) {
                UserRepository.loadUserInfo(context)
            }

            fetchAccounts()
            fetchCategories()
        }
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            delay(500)
            _accountsUiState.value =AccountsUiState.Loading
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getAllAccounts()
                if (response.isSuccessful) {
                    val accounts = response.body()?.toMutableList() ?: mutableListOf()
                    _accountsUiState.value = AccountsUiState.Success(accounts)
                    AccountRepository.myAccounts = accounts
                } else {
                    _accountsUiState.value = AccountsUiState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _accountsUiState.value = AccountsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchAccountDetails(accountId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context)
                    .getAccountDetails(accountId)
                println(response.body())

                if (response.isSuccessful) {
                    _selectedAccount.value = response.body()
                } else {
                    _selectedAccount.value = null
                }
            } catch (e: Exception) {
                _selectedAccount.value = null
            }
        }
    }


    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getAllCategories()
                if (response.isSuccessful) {
                    val categories = response.body().orEmpty()
                    _categories.value = categories
                    CategoryRepository.categories = categories
                } else {
                    Log.e("DashboardViewModel", "Categories fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error fetching categories: ${e.message}")
            }
        }
    }

}