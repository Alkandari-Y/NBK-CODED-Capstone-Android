package com.coded.capstone.respositories

import android.content.Context
import com.coded.capstone.data.responses.authentication.ValidateTokenResponse
import com.coded.capstone.data.responses.kyc.KYCResponse
import com.coded.capstone.providers.RetrofitInstance

object UserRepository {
    var userInfo: ValidateTokenResponse? = null
    var kyc: KYCResponse? = null

//    suspend fun loadUserInfo(context: Context) {
//        val response = RetrofitInstance.getAuthServiceProvider(context).getUserInfo()
//        if (response.isSuccessful) {
//            userInfo = response.body()
//        } else {
//            userInfo = null
//        }
//    }
}