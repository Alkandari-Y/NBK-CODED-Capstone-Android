package com.coded.capstone.respositories

import android.content.Context
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.providers.RetrofitInstance

object AccountRepository{
    var myAccounts: MutableList<AccountResponse> = mutableListOf()

    suspend fun createAccount(request: AccountCreateRequest, context: Context): Result<AccountResponse> {
        return try {
            val service = RetrofitInstance.getBankingServiceProvide(context)
            val response = service.createAccount(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    myAccounts.add(it)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCachedAccounts(): List<AccountResponse> = myAccounts
}