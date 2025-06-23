package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.responses.recommendation.BusinessPartnerResponse
import com.coded.capstone.data.responses.recommendation.PromotionResponse
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.providers.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PromotionDetailData(
    val promotion: PromotionResponse,
    val businessPartner: BusinessPartnerResponse,
    val isExpired: Boolean = false
)

sealed class PromotionDetailUiState {
    data object Loading : PromotionDetailUiState()
    data class Success(val offerData: PromotionDetailData) : PromotionDetailUiState()
    data class Error(val message: String) : PromotionDetailUiState()
}

class PromotionDetailViewModel(
    private val context: Context
) : ViewModel() {

    val uiState = mutableStateOf<PromotionDetailUiState>(PromotionDetailUiState.Loading)
    private val recommendationService = RetrofitInstance.getRecommendationServiceProvide(context)

    fun loadOfferDetails(offerId: Long) {
        viewModelScope.launch {
            try {
                uiState.value = PromotionDetailUiState.Loading

                // Get promotion details
                val promotionResponse = recommendationService.getPromotionById(offerId)
                if (!promotionResponse.isSuccessful) {
                    uiState.value = PromotionDetailUiState.Error("Failed to load offer details")
                    return@launch
                }

                val promotion = promotionResponse.body()!!

                // Get business partner details
                val businessResponse = recommendationService.getPartnerById(promotion.businessPartnerId)
                if (!businessResponse.isSuccessful) {
                    uiState.value = PromotionDetailUiState.Error("Failed to load business details")
                    return@launch
                }

                val businessPartner = businessResponse.body()!!

                // Check if offer is expired
                val isExpired = promotion.endDate.isBefore(LocalDate.now())

                val offerData = PromotionDetailData(
                    promotion = promotion,
                    businessPartner = businessPartner,
                    isExpired = isExpired
                )

                uiState.value = PromotionDetailUiState.Success(offerData)

            } catch (e: Exception) {
                Log.e("OfferDetailViewModel", "Error loading offer details: ${e.message}")
                uiState.value = PromotionDetailUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun isUserAuthenticated(): Boolean {
        val token = TokenManager.getToken(context)
        return token != null && !TokenManager.isAccessTokenExpired(context)
    }

    fun getBusinessImageResource(businessName: String): String {
        // Map business names to your existing ic_ drawable names
        return when (businessName.lowercase().replace(" ", "").replace("&", "")) {
            "jumeirahhotels", "jumeirah" -> "ic_jumeirah_hotels"
            "almosafer" -> "ic_almosafer"
            "cariboucoffee" -> "ic_caribou_coffee"
            "shakeshack" -> "ic_shake_shack"
            "kidzaniakuwait", "kidzania" -> "ic_kidzania"
            "voxcinemas", "vox" -> "ic_vox_cinemas"
            "kuwaitairways" -> "ic_kuwait_airways"
            "xciteelectronics", "xcite" -> "ic_xcite"
            "hm", "h&m" -> "ic_hm"
            "safathome", "safat" -> "ic_safat_home"
            "sparkgym", "spark" -> "ic_spark_gym"
            "theavenuesmall", "theavenues", "avenues" -> "ic_the_avenues"
            "360mall", "360" -> "ic_360_mall"
            // Default fallback for any other businesses
            else -> "default_offer_image"
        }
    }
}
