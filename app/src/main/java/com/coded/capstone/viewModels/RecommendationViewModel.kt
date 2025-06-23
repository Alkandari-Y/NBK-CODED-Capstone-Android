package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.partner.FavBusinessResponse
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.data.requests.partner.SetFavBusinessRequest
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.orEmpty

sealed class FavBusinessUiState {
    data object Idle : FavBusinessUiState()
    data object Loading : FavBusinessUiState()
    data class Success(val favBusinesses: FavBusinessResponse) : FavBusinessUiState()
    data class Error(val message: String) : FavBusinessUiState()
}
class RecommendationViewModel(
    private val context: Context
) : ViewModel() {


private val _recommendedCard = MutableStateFlow<AccountProductResponse?>(null)
val recommendedCard: StateFlow<AccountProductResponse?> = _recommendedCard

    private val _partners = MutableStateFlow<List<PartnerDto>>(emptyList())
    val partners: StateFlow<List<PartnerDto>> = _partners

    private val _favBusinessUiState = MutableStateFlow<FavBusinessUiState>(
        FavBusinessUiState.Idle)
    val favBusinessUiState: StateFlow<FavBusinessUiState> = _favBusinessUiState

    fun fetchRecommendedCard() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getRecommendationServiceProvide(context).getOnboardingRecommendation()
                if (response.isSuccessful) {
                    val recommendedCard = response.body()
                    _recommendedCard.value = recommendedCard
                } else {
                    Log.e("RecommendationViewModel", "Suggested Card fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecommendationViewModel", "Error fetching Suggested Card: ${e.message}")
            }
        }
    }

    fun fetchBusinessPartners(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getBankingServiceProvide(context).getBusinessPartners()
                if (response.isSuccessful) {
                    val businesses = response.body().orEmpty()
                    _partners.value = businesses
                    BusinessRepository.businesses = businesses
                } else {
                    Log.e("RecommendationViewModel", "Businesses fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecommendationViewModel", "Error fetching Businesses: ${e.message}")
            }
        }
    }

    fun submitFavoriteBusinesses(selectedBusinesses: List<String>) {
        if (selectedBusinesses.isEmpty()) return

        viewModelScope.launch {
            try {
                _favBusinessUiState.value = FavBusinessUiState.Loading
                val request = SetFavBusinessRequest(selectedBusinesses)
                Log.d("RecommendationViewModel", "Sending request: $request")

                val response = BusinessRepository.setFavBusinesses(request, context)
                Log.d("RecommendationViewModel", "Response: $response")

                response.onSuccess {
                    _favBusinessUiState.value = FavBusinessUiState.Success(it)
                    Log.d("RecommendationViewModel", "Successfully saved ${it.favBusinesses.size} favorite businesses")
                }.onFailure {
                    _favBusinessUiState.value = FavBusinessUiState.Error(it.message ?: "Unknown error")
                    Log.e("RecommendationViewModel", "Failed to save favorite categories: ${it.message}")
                }

            } catch (e: Exception) {
                _favBusinessUiState.value = FavBusinessUiState.Error(e.message ?: "Something went wrong")
                Log.e("HomeScreenViewModel", "Exception in submitFavoriteBusinesses: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}