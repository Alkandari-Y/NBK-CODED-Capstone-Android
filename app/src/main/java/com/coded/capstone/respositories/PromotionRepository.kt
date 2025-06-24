package com.coded.capstone.respositories

import android.content.Context
import com.coded.capstone.data.requests.partner.FavBusinessDto
import com.coded.capstone.data.requests.partner.FavBusinessResponse
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.data.requests.partner.SetFavBusinessRequest
import com.coded.capstone.data.responses.promotion.PromotionResponse
import com.coded.capstone.providers.RetrofitInstance

object PromotionRepository {
    var promotions = listOf<PromotionResponse>()
}