package com.coded.capstone.providers

import android.content.Context
import com.coded.capstone.providers.AuthServiceProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val AUTH_SERVICE_PORT = 8000
    private const val BANK_SERVICE_PORT = 8001
    private const val RECOMMENDATION_SERVER_PORT=8002

//    private const val DEVICE_BASE_URL = "http://192.168.123.54:"
//
//    private fun createOkHttpClient(context: Context): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(TokenInterceptor(context))
//            .build()
//    }
//
//    fun getAuthServiceProvider(context: Context): AuthServiceProvider {
//        return Retrofit.Builder()
//            .baseUrl(getBaseUrl(AUTH_SERVICE_PORT))
//            .client(createOkHttpClient(context))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(AuthServiceProvider::class.java)
//    }
//
//
//    private fun getBaseUrl(port: Int): String = "http://192.168.123.54:$port/"
//
//    private fun getDeviceBaseUrl(port: Int): String = "$DEVICE_BASE_URL$port/"


    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(context))
            .build()
    }

    fun getAuthServiceProvider(context: Context): AuthServiceProvider {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(AUTH_SERVICE_PORT))
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthServiceProvider::class.java)
    }


    private fun getBaseUrl(port: Int): String = "http://10.0.2.2:$port/"
}
