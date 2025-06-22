package com.coded.capstone.providers


import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.recommendation.FavCategoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationServiceProvider{

    // Categories
    @POST("/api/v1/fav/categories")
    suspend fun setFavCategories(
        @Body setFavCategoriesRequest: SetFavCategoryRequest,
    ): Response<FavCategoryResponse>
}

