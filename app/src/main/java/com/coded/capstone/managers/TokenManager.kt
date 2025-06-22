package com.coded.capstone.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.auth0.android.jwt.JWT
import com.coded.capstone.data.requests.authentication.RefreshRequest
import com.coded.capstone.data.responses.authentication.JwtContents
import com.coded.capstone.data.responses.authentication.JwtResponse
import com.coded.capstone.providers.RetrofitInstance


object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val ACCESS_TOKEN_KEY = "access"
    private const val REFRESH_TOKEN_KEY = "refresh"
    private const val REMEMBER_ME_KEY = "remember_me"
    private const val USER_ID = "user_id"


    fun getUserIdFromSharedPref(context: Context): Long {
        return getPrefs(context).getLong(USER_ID, 0)
    }

    fun setUserIdInSharedPref(context: Context, userId: Long) {
        getPrefs(context).edit() {
            putLong(USER_ID, userId)
        }
    }

    fun isRememberMeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(REMEMBER_ME_KEY, false)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: JwtResponse) {
        getPrefs(context).edit {
            putString(ACCESS_TOKEN_KEY, token.access)
            putString(REFRESH_TOKEN_KEY, token.refresh)
        }
    }

    fun getToken(context: Context): JwtResponse? {
        val prefs = getPrefs(context)
        val access = prefs.getString(ACCESS_TOKEN_KEY, null)
        val refresh = prefs.getString(REFRESH_TOKEN_KEY, null)
        return if (!access.isNullOrBlank() && !refresh.isNullOrBlank()) {
            JwtResponse(access, refresh)
        } else null
    }

    fun clearToken(context: Context) {
        getPrefs(context).edit {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            remove(REMEMBER_ME_KEY)
        }
    }

    fun isAccessTokenExpired(context: Context): Boolean {
        val token = getToken(context)?.access ?: return true
        return try {
            val jwt = JWT(token)
            jwt.isExpired(10)
        } catch (e: Exception) {
            true
        }
    }

    suspend fun refreshToken(context: Context): JwtResponse? {
        val refreshToken = getToken(context)?.refresh ?: return null
        return try {
            val response = RetrofitInstance.getAuthServiceProvider(context)
                .refreshToken(RefreshRequest(refresh = refreshToken))

            if (response.isSuccessful) {
                response.body()?.also {
                    saveToken(context, it)
                    Log.d("TokenManager", "Token refreshed successfully")
                }
            } else {
                Log.w("TokenManager", "Refresh failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "Refresh error: ${e.message}")
            null
        }
    }

    fun decodeAccessToken(context: Context): JwtContents? {
        val token = getToken(context)?.access ?: return null
        return try {
            val jwt = JWT(token)
            JwtContents(
                userId = jwt.getClaim("userId").asLong() ?: 0L,
                isActive = jwt.getClaim("isActive").asBoolean() ?: false,
                roles = jwt.getClaim("roles").asList(String::class.java) ?: emptyList(),
                type = jwt.getClaim("type").asString() ?: ""
            )
        } catch (e: Exception) {
            Log.e("TokenManager", "Failed to decode token: ${e.message}")
            null
        }
    }
}
