package com.coded.capstone.respositories

import android.content.Context
import com.coded.capstone.data.requests.partner.FavBusinessDto
import com.coded.capstone.data.requests.partner.FavBusinessResponse
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.data.requests.partner.SetFavBusinessRequest
import com.coded.capstone.providers.RetrofitInstance

object BusinessRepository {
    var businesses = listOf<PartnerDto>()
    var favBusinesses= listOf<FavBusinessDto>()

    suspend fun setFavBusinesses(request: SetFavBusinessRequest, context: Context): Result<FavBusinessResponse> {
        return try {
            val service = RetrofitInstance.getRecommendationServiceProvide(context)
            val response = service.setFavBusinesses(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    favBusinesses = it.favBusinesses
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