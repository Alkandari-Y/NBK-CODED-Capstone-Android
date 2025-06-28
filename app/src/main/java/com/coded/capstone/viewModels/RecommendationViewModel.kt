package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.partner.FavBusinessResponse
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.data.requests.partner.SetFavBusinessRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.data.responses.promotion.PromotionResponse
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.BusinessRepository
import com.coded.capstone.respositories.PromotionRepository
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

    private val _promotions = MutableStateFlow<List<PromotionResponse>>(emptyList())
    val promotions: StateFlow<List<PromotionResponse>> = _promotions

    private val _selectedPromotion = MutableStateFlow<PromotionResponse?>(null)
    val selectedPromotion: StateFlow<PromotionResponse?> = _selectedPromotion

    private val _promotionsByBusiness = MutableStateFlow<List<PromotionResponse>>(emptyList())
    val promotionsByBusiness:  StateFlow<List<PromotionResponse>> = _promotionsByBusiness

    private val _activePromotionsByBusiness = MutableStateFlow<List<PromotionResponse>>(emptyList())
    val activePromotionsByBusiness:  StateFlow<List<PromotionResponse>> = _activePromotionsByBusiness

    private val _favBusinessUiState = MutableStateFlow<FavBusinessUiState>(
        FavBusinessUiState.Idle)
    val favBusinessUiState: StateFlow<FavBusinessUiState> = _favBusinessUiState

    fun fetchRecommendedCard() {
        viewModelScope.launch {
            try {
                println("RecommendationViewModel: Starting API call to getOnboardingRecommendation")
                val response = RetrofitInstance.getRecommendationServiceProvide(context).getOnboardingRecommendation()
                println("RecommendationViewModel: API response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val recommendedCard = response.body()
                    println("RecommendationViewModel: Response body: $recommendedCard")
                    _recommendedCard.value = recommendedCard
                    
                    if (recommendedCard != null) {
                        println("RecommendationViewModel: Successfully set recommended card: ${recommendedCard.name}")
                    } else {
                        println("RecommendationViewModel: Warning - Response was successful but body is null")
                    }
                } else {
                    println("RecommendationViewModel: API call failed with code: ${response.code()}")
                    println("RecommendationViewModel: Error body: ${response.errorBody()?.string()}")
                    Log.e("RecommendationViewModel", "Suggested Card fetch failed: ${response.code()}")
                    // Set null to indicate failure
                    _recommendedCard.value = null
                }
            } catch (e: Exception) {
                println("RecommendationViewModel: Exception occurred: ${e.message}")
                e.printStackTrace()
                Log.e("RecommendationViewModel", "Error fetching Suggested Card: ${e.message}")
                // Set null to indicate failure
                _recommendedCard.value = null
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

                response.onSuccess { result ->
                    // Fetch recommended card before navigating
                    try {
                        val cardResponse = RetrofitInstance.getRecommendationServiceProvide(context).getOnboardingRecommendation()
                        if (cardResponse.isSuccessful) {
                            _recommendedCard.value = cardResponse.body()
                            _favBusinessUiState.value = FavBusinessUiState.Success(result)
                        } else {
                            _favBusinessUiState.value = FavBusinessUiState.Error("Failed to fetch recommended card")
                        }
                    } catch (e: Exception) {
                        _favBusinessUiState.value = FavBusinessUiState.Error("Failed to fetch recommended card: ${e.message}")
                    }
                }.onFailure {
                    _favBusinessUiState.value = FavBusinessUiState.Error(it.message ?: "Unknown error")
                    Log.e("RecommendationViewModel", "Failed to save favorite businesses: ${it.message}")
                }

            } catch (e: Exception) {
                _favBusinessUiState.value = FavBusinessUiState.Error(e.message ?: "Something went wrong")
                Log.e("RecommendationViewModel", "Exception in submitFavoriteBusinesses: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun fetchPromotions(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getRecommendationServiceProvide(context).getAllPromotions()
                if (response.isSuccessful) {
                    val promotions = response.body().orEmpty()
                    _promotions.value = promotions
                    PromotionRepository.promotions = promotions
                } else {
                    Log.e("RecommendationViewModel", "promotions fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecommendationViewModel", "Error fetching promotions: ${e.message}")
            }
        }
    }
    fun fetchPromotionDetails(promotionId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getRecommendationServiceProvide(context).getPromotionById(promotionId)
                println(response.body())

                if (response.isSuccessful) {
                    _selectedPromotion.value = response.body()
                } else {
                    _selectedPromotion.value = null
                }
            } catch (e: Exception) {
                _selectedPromotion.value = null
            }
        }
    }

    fun fetchPromotionsByBusiness(businessId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getRecommendationServiceProvide(context).getPromotionsByBusiness(businessId)
                println(response.body())
                if (response.isSuccessful) {
                    val promotions = response.body().orEmpty()
                    _promotionsByBusiness.value = promotions
                } else {
                    Log.e("RecommendationViewModel", "promotions fetch failed:  ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecommendationViewModel", "Error fetching promotions: ${e.message}")
            }
        }
    }
    fun fetchActivePromotionsByBusiness(businessId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getRecommendationServiceProvide(context).getActiveBusinessPromotions(businessId)
                println(response.body())
                if (response.isSuccessful) {
                    val promotions = response.body().orEmpty()
                    _activePromotionsByBusiness.value = promotions
                } else {
                    Log.e("RecommendationViewModel", "promotions fetch failed:  ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecommendationViewModel", "Error fetching promotions: ${e.message}")
            }
        }
    }

}