package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.Services.FirebaseToken
import com.coded.capstone.data.requests.authentication.LoginRequest
import com.coded.capstone.data.requests.authentication.RegisterCreateRequest
import com.coded.capstone.data.responses.authentication.JwtContents
import com.coded.capstone.data.responses.authentication.JwtResponse
import com.coded.capstone.data.responses.errors.ApiErrorResponse
import com.coded.capstone.data.responses.errors.ValidationError
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


sealed class AuthUiState {
    data object Loading : AuthUiState()
    data class Success(val jwtResponse: JwtResponse) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class AuthResultState {
    data object Idle : AuthResultState()
    data object Loading : AuthResultState()
    data object Success : AuthResultState()
    data class Error(val message: String) : AuthResultState()
}
class AuthViewModel(
    private val context: Context

): ViewModel()  {
    val uiState = mutableStateOf<AuthUiState>(AuthUiState.Loading)
    val registerFieldErrors = mutableStateOf<List<ValidationError>>(emptyList())
    private val authApiService = RetrofitInstance.getAuthServiceProvider(context)
    private val notificationApiService = RetrofitInstance.getNotificationServiceProvide(context)
    var token = mutableStateOf<JwtResponse?>(null)
    var decodedToken = mutableStateOf<JwtContents?>(null)

    fun loadStoredToken() {
        val storedToken = TokenManager.getToken(context)
        if (storedToken != null) {
            token.value = storedToken
            decodedToken.value = TokenManager.decodeAccessToken(context)
            Log.d("Token", "Loaded token and decoded: ${decodedToken.value}")
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            uiState.value = AuthUiState.Loading
            try {
                val response = authApiService.register(
                    RegisterCreateRequest(
                        username = username,
                        email = email,
                        password = password,
                    )
                )
                if (response.isSuccessful) {
                    val jwtResponse = response.body()
                    if (jwtResponse != null) {
                        token.value = jwtResponse
                        TokenManager.saveToken(context, jwtResponse)
                        decodedToken.value = TokenManager.decodeAccessToken(context)
                        UserRepository.loadUserInfo(context)
                        uiState.value = AuthUiState.Success(jwtResponse)
                    }
                } else {
                    if (response.code() == 400) {
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, ApiErrorResponse::class.java)

                        registerFieldErrors.value = errorResponse.fieldErrors ?: emptyList()
                        uiState.value = AuthUiState.Error(errorResponse.message)
                    } else {
                        uiState.value = AuthUiState.Error("Registration failed: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                uiState.value = AuthUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            uiState.value = AuthUiState.Loading
            try {
                val response = authApiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val jwtResponse = response.body()
                    if (jwtResponse != null) {
                        token.value = jwtResponse
                        TokenManager.saveToken(context, jwtResponse)
                        decodedToken.value = TokenManager.decodeAccessToken(context)
                        
                        // Load user info after successful login
                        UserRepository.loadUserInfo(context)

                        val fcmToken = Firebase.messaging.token.await()
                        Log.d("FCM", "FCM token = $fcmToken")
                        val result = notificationApiService.testToken(FirebaseToken(token = fcmToken))
                        if (result.isSuccessful) {
                            Log.d("FCM", "Token sent successfully.")
                        } else {
                            Log.w("FCM", "Failed to send token. Code: ${result.code()}")
                        }

                        // Fetch KYC after login with proper error handling
                        UserRepository.kyc = null
                        try {
                            Log.d("AuthViewModel", "Fetching KYC data after login...")
                            val kycResponse = RetrofitInstance.getBankingServiceProvide(context).getUserKyc()
                            if (kycResponse.isSuccessful) {
                                val kycData = kycResponse.body()
                                if (kycData != null) {
                                    UserRepository.kyc = kycData
                                    Log.d("AuthViewModel", "KYC data loaded successfully: ${kycData.firstName} ${kycData.lastName}")
                                } else {
                                    Log.w("AuthViewModel", "KYC response body is null")
                                }
                            } else {
                                Log.w("AuthViewModel", "Failed to fetch KYC: ${kycResponse.code()} - ${kycResponse.message()}")
                            }
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error fetching KYC: ${e.message}")
                        }
                        
                        uiState.value = AuthUiState.Success(jwtResponse)
                    }
                } else {
                    uiState.value = AuthUiState.Error(
                        when (response.code()) {
                            401 -> "Invalid credentials"
                            500 -> "Server error"
                            else -> "Unknown error"
                        }
                    )
                }
            } catch (e: Exception) {
                uiState.value = AuthUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun logout() {
        TokenManager.clearToken(context)
        token.value = null
        decodedToken.value = null
        uiState.value = AuthUiState.Loading
        // Clear KYC on logout
        UserRepository.kyc = null
    }

}