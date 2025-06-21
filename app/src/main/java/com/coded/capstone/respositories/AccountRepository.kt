package com.coded.capstone.respositories

import android.content.Context
import com.coded.capstone.data.requests.account.AccountCreateRequest
import com.coded.capstone.data.responses.account.AccountCreateResponse
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.providers.RetrofitInstance

object AccountRepository{
    var myAccounts: MutableList<AccountResponse> = mutableListOf()

    suspend fun createAccount(request: AccountCreateRequest, context: Context): Result<AccountCreateResponse> {
        return try {
            val service = RetrofitInstance.getBankingServiceProvide(context)
            val response = service.createAccount(request)
            if (response.isSuccessful) {
                val body = response.body()!!

                val accountResponse = AccountResponse(
                    id = body.id,
                    accountNumber = body.accountNumber,
                    balance = body.balance,
                    ownerId = body.ownerId,
                    ownerType = body.ownerType,
                    accountProductId = body.accountProduct.id,
                    accountType = body.accountType
                )

                myAccounts.add(accountResponse)
                Result.success(body)
            } else {
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCachedAccounts(): List<AccountResponse> = myAccounts
}