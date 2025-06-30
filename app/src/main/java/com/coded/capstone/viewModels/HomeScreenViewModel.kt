package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.payment.PaymentCreateRequest
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.kyc.KYCResponse
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.data.responses.recommendation.FavCategoryResponse
import com.coded.capstone.data.responses.transaction.TransactionDetails
import com.coded.capstone.data.responses.xp.UserXpInfoResponse
import com.coded.capstone.data.responses.xp.XpHistoryDto
import com.coded.capstone.data.responses.xp.XpTierResponse
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountProductRepository
import com.coded.capstone.respositories.AccountRepository
import com.coded.capstone.respositories.CategoryRepository
import com.coded.capstone.respositories.UserRepository
import com.coded.capstone.respositories.XpRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    private lateinit var statusText: TextView

    // Accounts
    private val _accountsUiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val accountsUiState: StateFlow<AccountsUiState> = _accountsUiState

    private val _selectedAccount = MutableStateFlow<AccountResponse?>(null)
    val selectedAccount: StateFlow<AccountResponse?> = _selectedAccount

    // Categories
    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    private val _favCategoryUiState = MutableStateFlow<FavCategoryUiState>(
        FavCategoryUiState.Idle)
    val favCategoryUiState: StateFlow<FavCategoryUiState> = _favCategoryUiState

    // xp tier
    private val _xpTiers = MutableStateFlow<List<XpTierResponse>>(emptyList())
    val xpTiers: StateFlow<List<XpTierResponse>> = _xpTiers

    private val _selectedTier = MutableStateFlow<XpTierResponse?>(null)
    val selectedTier: StateFlow<XpTierResponse?> = _selectedTier

    private val _userXp = MutableStateFlow<UserXpInfoResponse?>(null)
    val userXp: StateFlow<UserXpInfoResponse?> = _userXp

    private val _userXpHistory = MutableStateFlow<List<XpHistoryDto>>(emptyList())
    val userXpHistory: StateFlow<List<XpHistoryDto>> = _userXpHistory

    // transactions
    private val _transactions = MutableStateFlow<List<TransactionDetails>>(emptyList())
    val transactions: StateFlow<List<TransactionDetails>> = _transactions

    // Account products
    private val _perksOfAccountProduct = MutableStateFlow<List<PerkDto>>(emptyList())
    val perksOfAccountProduct:  StateFlow<List<PerkDto>> = _perksOfAccountProduct


    private val _accountProducts = MutableStateFlow<List<AccountProductResponse>>(emptyList())
    val accountProducts: StateFlow<List<AccountProductResponse>> = _accountProducts

    //  KYC StateFlow
    private val _kyc = MutableStateFlow<KYCResponse?>(null)
    val kyc: StateFlow<KYCResponse?> = _kyc

    private val _nfcPayload = MutableStateFlow<String>("NFC data not read")
    val nfcPayload: StateFlow<String> = _nfcPayload

    // Flag to prevent duplicate API calls
    private var accountsFetched = false

    init {
        viewModelScope.launch {
            if (UserRepository.userInfo == null) {
                UserRepository.loadUserInfo(context)
            }
            fetchAccounts()
            fetchCategories()
            fetchAccountProducts()
            fetchKyc()
            fetchXpTiers()
        }
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            _accountsUiState.value = AccountsUiState.Loading
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getAllAccounts()
                if (response.isSuccessful) {
                    val accounts = response.body()?.toMutableList() ?: mutableListOf()
                    // Debug log: print account numbers and balances
                    accounts.forEach { acc ->
                        Log.d("AccountFetchDebug", "Account: ${acc.accountNumber}, Balance: ${acc.balance}")
                    }
                    _accountsUiState.value = AccountsUiState.Success(accounts)
                    AccountRepository.myAccounts = accounts
                    accountsFetched = true
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

    fun fetchPerksOfAccountProduct(productId:String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getPerksOfAccountProduct(productId)
                println(response.body())
                if (response.isSuccessful) {
                    val perks = response.body().orEmpty()
                    _perksOfAccountProduct.value = perks
                } else {
                    Log.e("HomeScreenViewModel", "perks fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching perks: ${e.message}")
            }
        }
    }

    fun fetchTransactionHistory(accountNumber: String) {
        viewModelScope.launch {
            try {
                Log.d("TransactionFetch", "Fetching transactions for account: $accountNumber")
                val response = RetrofitInstance.getBankingServiceProvide(context).getAllTransactionsByAccountNumber(accountNumber)
                Log.d("TransactionFetch", "Response code: ${response.code()}")
                Log.d("TransactionFetch", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val transactions = response.body().orEmpty()
                    Log.d("TransactionFetch", "Fetched ${transactions.size} transactions")
                    _transactions.value = transactions
                } else {
                    Log.e("TransactionFetch", "Transaction fetch failed: ${response.code()}")
                    Log.e("TransactionFetch", "Error body: ${response.errorBody()?.string()}")
                    _transactions.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("TransactionFetch", "Error fetching transactions", e)
                _transactions.value = emptyList()
            }
        }
    }

    fun fetchKyc() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getUserKyc()
                if (response.isSuccessful) {
                    val kycData = response.body()
                    if (kycData != null) {
                        UserRepository.kyc = kycData
                        _kyc.value = kycData
                    } else {
                        Log.w("HomeScreenViewModel", "KYC response body is null")
                    }
                } else {
                    Log.w("HomeScreenViewModel", "Failed to fetch KYC: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching KYC: ${e.message}")
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

    fun fetchXpTiers(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getAllXpTiers()
                if (response.isSuccessful) {
                    val xpTiers = response.body().orEmpty()
                    _xpTiers.value = xpTiers
                    XpRepository.xpTiers = xpTiers
                } else {
                    Log.e("HomeScreenViewModel", "xp tiers fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching xp tiers: ${e.message}")
            }
        }
    }

    fun getXpTierById(tierId:String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context)
                    .getXpTierById(tierId)
                println(response.body())

                if (response.isSuccessful) {
                    _selectedTier.value = response.body()
                } else {
                    _selectedTier.value = null
                }
            } catch (e: Exception) {
                _selectedTier.value = null
            }
        }
    }

    fun getUserXpInfo(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context)
                    .getUserXpInfo()
                println(response.body())

                if (response.isSuccessful) {
                    _userXp.value = response.body()
                } else {
                    _userXp.value = null
                }
            } catch (e: Exception) {
                _userXp.value = null
            }
        }
    }

    fun getUserXpHistory(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getUserXpHistory()
                if (response.isSuccessful) {
                    val userXpHistory = response.body().orEmpty()
                    _userXpHistory.value = userXpHistory
                    XpRepository.xpHistory = userXpHistory
                } else {
                    Log.e("HomeScreenViewModel", "xp history fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching xp history: ${e.message}")
            }
        }
    }



    // purchase

    fun handleNfcPayload(payload: String) {
        statusText.text = "NFC tag read!"
        try {
            val json = JSONObject(payload)
            val sourceAccountNumber = selectedAccount.value?.accountNumber
            val destinationAccountNumber = json.getString("destinationAccountNumber")
            val amount = json.getString("amount")

            val requestBody = PaymentCreateRequest(
                sourceAccountNumber = sourceAccountNumber?:"",
                destinationAccountNumber = destinationAccountNumber,
                amount = amount.toBigDecimal(),
            )

            // Send purchase request to backend
            makePurchase(requestBody)

        } catch (e: Exception) {
            statusText.text = "Invalid NFC payload"
        }
    }

    fun makePurchase(request: PaymentCreateRequest) {
        viewModelScope.launch{
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).createPayment(request)
                if (response.isSuccessful) {
                    val nfcPayload = response.body().toString()
                    _nfcPayload.value = nfcPayload
                } else {
                    Log.e("HomeScreenViewModel", "xp history fetch failed: ${response.code()}")
                }
            }catch (e: Exception) {
//                _accountUiState.value = AccountCreateUiState.Error(e.message ?: "Something went wrong")
            }
        }

    }
}
