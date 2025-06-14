package com.coded.capstone.providers

import android.content.Context
import android.util.Log
import com.coded.capstone.managers.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var token = TokenManager.getToken(context)?.access

        val request = chain.request().newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
        }.build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.w("TokenInterceptor", "Unauthorized – clearing tokens")
            TokenManager.clearToken(context)
        }

        return response
    }
}
