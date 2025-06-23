package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.orEmpty


class RecommendationViewModel(
    private val context: Context
) : ViewModel() {


private val _recommendedCard = MutableStateFlow<AccountProductResponse?>(null)
val recommendedCard: StateFlow<AccountProductResponse?> = _recommendedCard

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

}