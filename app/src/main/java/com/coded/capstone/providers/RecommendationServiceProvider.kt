package com.coded.capstone.providers


import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.requests.ble.BlueToothBeaconNotificationRequest
import com.coded.capstone.data.requests.partner.FavBusinessResponse
import com.coded.capstone.data.requests.partner.SetFavBusinessRequest
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.promotion.PromotionResponse
import com.coded.capstone.data.responses.recommendation.FavCategoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RecommendationServiceProvider{

    // Categories
    @POST("/api/v1/fav/categories")
    suspend fun setFavCategories(
        @Body setFavCategoriesRequest: SetFavCategoryRequest,
    ): Response<FavCategoryResponse>

    // Business
    @POST("api/v1/fav/businesses")
    suspend fun setFavBusinesses(
        @Body setFavBusinessRequest: SetFavBusinessRequest,
    ): Response<FavBusinessResponse>

    // Recommendation
    @GET("/api/v1/recommendations/onBoarding")
    suspend fun getOnboardingRecommendation(): Response<AccountProductResponse>

    // Promotion
    @GET("/api/v1/promotions")
    suspend fun getAllPromotions(): Response<List<PromotionResponse>>

    @GET("api/v1/promotions/{promotionId}")

    suspend fun getPromotionById( @Path("promotionId") promotionId: String): Response<PromotionResponse>

    @GET("api/v1/promotions/business/{businessId}")
    suspend fun getPromotionsByBusiness( @Path("businessId") businessId: String): Response<List<PromotionResponse>>

    @GET("api/v1/promotions/business/{businessId}/active")
    suspend fun getActiveBusinessPromotions(@Path("businessId") businessId: String): Response<List<PromotionResponse>>
}


    // ble
    @POST("/api/v1/recommendations/bluetooth-beacon")
    suspend fun sendBleDevice(@Body payload: BlueToothBeaconNotificationRequest): Response<Void>
}
