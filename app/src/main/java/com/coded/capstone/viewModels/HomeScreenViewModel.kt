package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.data.responses.account.AccountProduct
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.recommendation.FavCategoryDto
import com.coded.capstone.data.responses.recommendation.FavCategoryResponse
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountProductRepository
import com.coded.capstone.respositories.AccountRepository
import com.coded.capstone.respositories.CategoryRepository
import com.coded.capstone.respositories.UserRepository
import com.coded.capstone.viewModels.AccountViewModel.AccountCreateUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.orEmpty

sealed class AccountsUiState {
    data object Loading : AccountsUiState()
    data class Success(val accounts: List<AccountResponse>) : AccountsUiState()
    data class Error(val message: String) : AccountsUiState()
}

sealed class FavCategoryUiState {
    data object Idle : FavCategoryUiState()
    data object Loading : FavCategoryUiState()
    data class Success(val favCategories: FavCategoryResponse) : FavCategoryUiState()
    data class Error(val message: String) : FavCategoryUiState()
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

    private val _favCategoryUiState = MutableStateFlow<FavCategoryUiState>(
        FavCategoryUiState.Idle)
    val favCategoryUiState: StateFlow<FavCategoryUiState> = _favCategoryUiState

    private val _accountProducts = MutableStateFlow<List<AccountProduct>>(emptyList())
    val accountProducts: StateFlow<List<AccountProduct>> = _accountProducts

    init {
        viewModelScope.launch {
            if (UserRepository.userInfo == null) {
                UserRepository.loadUserInfo(context)
            }
            fetchAccounts()
            fetchCategories()
            fetchAccountProducts()
        }
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            delay(500)
            _accountsUiState.value = AccountsUiState.Loading
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
                    Log.e("HomeScreenViewModel", "Categories fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching categories: ${e.message}")
            }
        }
    }

    fun fetchAccountProducts() {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.getBankingServiceProvide(context).getAllAccountProducts()
                if (response.isSuccessful) {
                    val accountProdutcs = response.body().orEmpty()
                    _accountProducts.value = accountProdutcs
                    AccountProductRepository.accountProducts = accountProdutcs
                } else {
                    Log.e(
                        "HomeScreenViewModel",
                        "Account Products fetch failed: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching account products: ${e.message}")
            }
        }
    }


    fun submitFavoriteCategories(selectedCategories: List<String>) {
        if (selectedCategories.isEmpty()) return

        viewModelScope.launch {
            try {
                _favCategoryUiState.value = FavCategoryUiState.Loading
                val request = SetFavCategoryRequest(selectedCategories)
                Log.d("HomeScreenViewModel", "Sending request: $request")
                
                val response = CategoryRepository.setFavCategories(request, context)
                Log.d("HomeScreenViewModel", "Response: $response")

                response.onSuccess {
                    _favCategoryUiState.value = FavCategoryUiState.Success(it)
                    Log.d("HomeScreenViewModel", "Successfully saved ${it.favCategories.size} favorite categories")
                }.onFailure {
                    _favCategoryUiState.value = FavCategoryUiState.Error(it.message ?: "Unknown error")
                    Log.e("HomeScreenViewModel", "Failed to save favorite categories: ${it.message}")
                }

            } catch (e: Exception) {
                _favCategoryUiState.value = FavCategoryUiState.Error(e.message ?: "Something went wrong")
                Log.e("HomeScreenViewModel", "Exception in submitFavoriteCategories: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}