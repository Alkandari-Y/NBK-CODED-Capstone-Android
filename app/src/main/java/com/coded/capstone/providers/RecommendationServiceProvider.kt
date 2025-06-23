package com.coded.capstone.providers

import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.data.responses.recommendation.BusinessPartnerResponse
import com.coded.capstone.data.responses.recommendation.FavCategoryResponse
import com.coded.capstone.data.responses.recommendation.PromotionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RecommendationServiceProvider {

    // Categories
    @POST("/api/v1/fav/categories")
    suspend fun setFavCategories(
        @Body setFavCategoriesRequest: SetFavCategoryRequest,
    ): Response<FavCategoryResponse>

    // Partners
    @GET("/api/v1/partners/{partnerId}")
    suspend fun getPartnerById(
        @Path("partnerId") partnerId: Long
    ): Response<BusinessPartnerResponse>

    // Promotions (Offers)
    @GET("/api/v1/promotions/{id}")
    suspend fun getPromotionById(
        @Path("id") promotionId: Long
    ): Response<PromotionResponse>

    @GET("/api/v1/promotions/business/{businessId}")
    suspend fun getPromotionsByBusiness(
        @Path("businessId") businessId: Long
    ): Response<List<PromotionResponse>>

}