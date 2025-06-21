package com.coded.capstone.respositories

import android.content.Context
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.recommendation.FavCategoryDto
import com.coded.capstone.data.responses.recommendation.FavCategoryResponse
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountRepository.myAccounts
import kotlin.collections.listOf

object CategoryRepository {
    var categories = listOf<CategoryDto>()
    var favCategories= listOf<FavCategoryDto>()

    suspend fun setFavCategories(request: SetFavCategoryRequest, context: Context): Result<FavCategoryResponse> {
        return try {
            val service = RetrofitInstance.getRecommendationServiceProvide(context)
            val response = service.setFavCategories(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    favCategories = it.favCategories
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Server error: ${response.code()} ${response.body()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}