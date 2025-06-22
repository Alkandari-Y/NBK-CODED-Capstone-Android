//package com.coded.capstone.viewModels
//
//
//
//import android.content.Context
//
//import android.util.Log
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.coded.capstone.data.requests.kyc.KYCRequest
//import com.coded.capstone.data.responses.kyc.KYCResponse
//import com.coded.capstone.formstates.kyc.KycFormState
//import com.coded.capstone.managers.TokenManager
//import com.coded.capstone.providers.RetrofitInstance
//import com.coded.capstone.respositories.UserRepository
//
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//
//import java.math.BigDecimal
//
//
//sealed class UiStatus {
//    object Idle : UiStatus()
//    object Loading : UiStatus()
//    object Success : UiStatus()
//    data class Error(val message: String) : UiStatus()
//}
//
//class KycViewModel(private val context: Context): ViewModel() {
//
//    var formState = mutableStateOf(KycFormState())
//    var isEditMode = mutableStateOf(false)
//
//    private val _status = MutableStateFlow<UiStatus>(UiStatus.Idle)
//    val status: StateFlow<UiStatus> = _status
//
//    private val bankingService = RetrofitInstance.getBankingServiceProvide(context)
//
//    init {
//        Log.d("KYC_DEBUG", "Initializing KycViewModel")
//        isEditMode.value = true  // Set to true by default for new users
//        initializeKycIfAvailable()
//    }
//
//    private fun initializeKycIfAvailable() {
//        val tokenInfo = TokenManager.decodeAccessToken(context)
//        Log.d("KYC_DEBUG", "Token info: $tokenInfo")
//
//        if (tokenInfo?.isActive == true) {
//            viewModelScope.launch {
//                _status.value = UiStatus.Loading
//                try {
//                    Log.d("KYC_DEBUG", "Calling getUserKyc()")
//                    val response = bankingService.getUserKyc()
//                    Log.d("KYC_DEBUG", "KYC Response: ${response.body()}")  ;
//                    if (response.isSuccessful) {
//                        response.body()?.let { kyc ->
//                            loadKyc(kyc)
//                            isEditMode.value = false
//                            _status.value = UiStatus.Idle
//                        } ?: run {
//                            Log.d("KYC_DEBUG", "No KYC data found, enabling edit mode")
//                            isEditMode.value = true
//                            _status.value = UiStatus.Idle
//                        }
//                    } else {
//                        Log.d("KYC_DEBUG", "Failed to load KYC: ${response.code()}, enabling edit mode")
//                        isEditMode.value = true
//                        _status.value = UiStatus.Idle
//                    }
//                } catch (e: Exception) {
//                    Log.d("KYC_DEBUG", "Error fetching KYC: ${e.message}, enabling edit mode")
//                    isEditMode.value = true
//                    _status.value = UiStatus.Idle
//                }
//            }
//        } else {
//            Log.d("KYC_DEBUG", "No active token, enabling edit mode")
//            isEditMode.value = true
//        }
//    }
//
//    fun loadKyc(data: KYCResponse) {
//        formState.value = KycFormState(
//            firstName = data.firstName,
//            lastName = data.lastName,
//            dateOfBirth = data.dateOfBirth.orEmpty(),
//            salary = data.salary.toPlainString(),
//            nationality = data.nationality,
//            civilId = data.civilId,
//            mobileNumber = data.mobileNumber
//        )
//    }
//
//    fun submitKyc() {
//        formState.value = formState.value.validate()
//        if (!formState.value.formIsValid) return
//
//        viewModelScope.launch {
//            _status.value = UiStatus.Loading
//            try {
//                val request = KYCRequest(
//                    firstName = formState.value.firstName,
//                    lastName = formState.value.lastName,
//                    dateOfBirth = formState.value.dateOfBirth,
//                    salary = formState.value.salary.toBigDecimalOrNull() ?: BigDecimal.ZERO,
//                    nationality = formState.value.nationality,
//                    civilId = formState.value.civilId,
//                    mobileNumber = formState.value.mobileNumber
//                )
//                val updatedKyc: KYCResponse =
//                    RetrofitInstance.getBankingServiceProvide(context).updateKyc(request)
//
//                UserRepository.kyc = updatedKyc
//                TokenManager.refreshToken(context)
//
//                loadKyc(updatedKyc)
//                initializeKycIfAvailable()
//
//                _status.value = UiStatus.Success
//                isEditMode.value = false
//            } catch (e: Exception) {
//                _status.value = UiStatus.Error("KYC update failed: ${e.message}")
//            }
//        }
//    }
//}