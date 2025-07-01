package com.coded.capstone.providers

import android.content.Context
import com.coded.capstone.data.requests.deeplink.DeepLinkRequest
import com.coded.capstone.data.responses.deeplink.DeepLinkResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API interface for deep link operations
 */
interface DeepLinkApiService {
    
    /**
     * Process deep link request and return appropriate response
     */
    @POST("/api/v1/deeplink/process")
    suspend fun processDeepLink(
        @Body request: DeepLinkRequest
    ): Response<DeepLinkResponse>
    
    /**
     * Generate deep link URLs for sharing
     */
    @POST("/api/v1/deeplink/generate")
    suspend fun generateDeepLink(
        @Body request: DeepLinkRequest
    ): Response<DeepLinkResponse>
    
    /**
     * Validate deep link format
     */
    @POST("/api/v1/deeplink/validate")
    suspend fun validateDeepLink(
        @Body request: DeepLinkRequest
    ): Response<DeepLinkResponse>
}

/**
 * Service provider for deep link operations
 */
class DeepLinkServiceProvider(private val context: Context) {
    
    private val apiService: DeepLinkApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(context))
            .build()
            
        Retrofit.Builder()
            .baseUrl("http://192.168.22.54:8000/") // Using auth service port
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeepLinkApiService::class.java)
    }
    
    /**
     * Process a deep link and get navigation information
     */
    suspend fun processDeepLink(deepLink: String): Result<DeepLinkResponse> {
        return try {
            val request = DeepLinkRequest(deepLink = deepLink)
            val response = apiService.processDeepLink(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to process deep link: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate a deep link for a specific screen with parameters
     */
    suspend fun generateDeepLink(
        targetScreen: String,
        parameters: Map<String, String>? = null
    ): Result<DeepLinkResponse> {
        return try {
            val request = DeepLinkRequest(
                targetScreen = targetScreen,
                parameters = parameters
            )
            val response = apiService.generateDeepLink(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to generate deep link: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Validate a deep link format
     */
    suspend fun validateDeepLink(deepLink: String): Result<DeepLinkResponse> {
        return try {
            val request = DeepLinkRequest(deepLink = deepLink)
            val response = apiService.validateDeepLink(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Invalid deep link format: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 