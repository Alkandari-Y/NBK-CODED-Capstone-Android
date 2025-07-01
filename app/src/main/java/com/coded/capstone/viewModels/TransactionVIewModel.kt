package com.coded.capstone.viewModels

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.transaction.TransferCreateRequest
import com.coded.capstone.data.requests.transaction.PaymentCreateRequest
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.states.TransferUiState
import com.coded.capstone.data.states.TopUpUiState
import com.coded.capstone.providers.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal
import java.nio.charset.Charset
import java.util.Arrays

class TransactionViewModel(private val context: Context) : ViewModel() {

    companion object {
        private const val TAG = "TransactionViewModel"
    }

    private val _transferUiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val transferUiState: StateFlow<TransferUiState> = _transferUiState

    private val _topUpUiState = MutableStateFlow<TopUpUiState>(TopUpUiState.Idle)
    val topUpUiState: StateFlow<TopUpUiState> = _topUpUiState

    // NFC Payment States
    private val _nfcState = MutableStateFlow<NfcState>(NfcState.Idle)
    val nfcState: StateFlow<NfcState> = _nfcState

    private val _lastPaymentData = MutableStateFlow<PaymentData?>(null)
    val lastPaymentData: StateFlow<PaymentData?> = _lastPaymentData

    // Track current selected account for NFC payments
    private val _currentSelectedAccount = MutableStateFlow<AccountResponse?>(null)
    val currentSelectedAccount: StateFlow<AccountResponse?> = _currentSelectedAccount

    fun resetTransferState() {
        _transferUiState.value = TransferUiState.Idle
    }

    fun resetTopUpState() {
        _topUpUiState.value = TopUpUiState.Idle
    }

    // ============= NFC PAYMENT METHODS =============

    /**
     * Set the current selected account for NFC payments
     */
    fun setCurrentSelectedAccount(account: AccountResponse) {
        _currentSelectedAccount.value = account
        Log.d(TAG, "Current account set for NFC payments: ${account.accountNumber}")
    }

    /**
     * Activate NFC listening mode
     */
    fun activateNfcListening() {
        _nfcState.value = NfcState.Listening
        Log.d(TAG, "NFC listening activated")
    }

    /**
     * Deactivate NFC listening mode
     */
    fun deactivateNfcListening() {
        _nfcState.value = NfcState.Idle
        Log.d(TAG, "NFC listening deactivated")
    }

    /**
     * Reset NFC state
     */
    fun resetNfcState() {
        _nfcState.value = NfcState.Idle
        _lastPaymentData.value = null
        Log.d(TAG, "NFC state reset")
    }

    /**
     * Process NFC tag when detected
     */
    fun processNfcTag(tag: Tag) {
        // Only process if NFC is in listening state
        if (_nfcState.value != NfcState.Listening) {
            Log.d(TAG, "NFC tag detected but not in listening mode - ignoring")
            return
        }

        viewModelScope.launch {
            _nfcState.value = NfcState.Processing
            try {
                val paymentData = parseNfcPaymentData(tag)
                _lastPaymentData.value = paymentData

                // Submit payment to backend
                val result = submitNfcPaymentToBackend(paymentData)

                if (result.success) {
                    _nfcState.value = NfcState.Success(result)
                    Log.d(TAG, "NFC Payment processed successfully: $result")
                } else {
                    _nfcState.value = NfcState.Error(result.message)
                    Log.e(TAG, "NFC Payment failed: ${result.message}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "NFC processing failed", e)
                _nfcState.value = NfcState.Error(e.message ?: "NFC processing failed")
            }
        }
    }

    /**
     * Parse payment data from NFC tag
     */
    private suspend fun parseNfcPaymentData(tag: Tag): PaymentData {
        return withContext(Dispatchers.IO) {
            val ndef = Ndef.get(tag) ?: throw IllegalArgumentException("Tag is not NDEF formatted")

            try {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                    ?: throw IllegalArgumentException("No NDEF message found on tag")

                parseNdefRecords(ndefMessage)
            } finally {
                try {
                    ndef.close()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to close NDEF connection", e)
                }
            }
        }
    }

    /**
     * Parse NDEF records to extract payment data
     */
    private fun parseNdefRecords(ndefMessage: NdefMessage): PaymentData {
        ndefMessage.records.forEach { record ->
            when (record.tnf) {
                NdefRecord.TNF_MIME_MEDIA -> {
                    val mimeType = String(record.type)
                    Log.d(TAG, "Found MIME record: $mimeType")

                    return when (mimeType) {
                        "application/json" -> parseJsonPaymentData(record.payload)
                        "text/plain" -> parseTextPaymentData(String(record.payload, Charset.forName("UTF-8")))
                        else -> throw IllegalArgumentException("Unsupported MIME type: $mimeType")
                    }
                }
                NdefRecord.TNF_WELL_KNOWN -> {
                    if (Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                        Log.d(TAG, "Found text record")
                        val textData = readTextRecord(record)
                        return parseTextPaymentData(textData)
                    }
                }
            }
        }
        throw IllegalArgumentException("No valid payment data found in NFC tag")
    }

    // JSON parsing - simplified
    private fun parseJsonPaymentData(payload: ByteArray): PaymentData {
        val jsonString = String(payload, Charset.forName("UTF-8"))
        Log.d(TAG, "Parsing JSON: $jsonString")

        val json = JSONObject(jsonString)

        return PaymentData(
            destinationAccount = json.getString("destination_account"),
            amount = json.getString("amount")
        )
    }

    // Text parsing - simplified
    private fun parseTextPaymentData(text: String): PaymentData {
        Log.d(TAG, "Parsing text: $text")

        val pairs = text.split(",").associate { pair ->
            val parts = pair.split("=", limit = 2)
            if (parts.size != 2) throw IllegalArgumentException("Invalid key-value pair: $pair")
            parts[0].trim() to parts[1].trim()
        }

        return PaymentData(
            destinationAccount = pairs["destination_account"]
                ?: throw IllegalArgumentException("Missing destination_account"),
            amount = pairs["amount"]
                ?: throw IllegalArgumentException("Missing amount")
        )
    }
    /**
     * Read text from RTD_TEXT record
     */
    private fun readTextRecord(record: NdefRecord): String {
        val payload = record.payload
        val languageCodeLength = payload[0].toInt() and 0x3F
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            Charset.forName("UTF-8")
        )
    }

    /**
     * Submit NFC payment to backend using existing API
     */
    private suspend fun submitNfcPaymentToBackend(paymentData: PaymentData): PaymentResult {
        return withContext(Dispatchers.IO) {
            try {
                val currentAccount = _currentSelectedAccount.value
                    ?: throw IllegalStateException("No account selected for payment")

                // Create the payment request using existing data structure
                val paymentRequest = PaymentCreateRequest(
                    sourceAccountNumber = currentAccount.accountNumber ?: "",
                    destinationAccountNumber = paymentData.destinationAccount,
                    amount = paymentData.amount.toBigDecimalSafe()
                )

                Log.d(TAG, "Making NFC payment API call: $paymentRequest")

                // Use existing RetrofitInstance and API
                val response = RetrofitInstance.getBankingServiceProvide(context).purchase(paymentRequest)

                if (response.isSuccessful) {
                    val paymentDetails = response.body()
                    PaymentResult(
                        success = true,
                        transactionId = paymentDetails?.transactionId ?: "TXN_${System.currentTimeMillis()}",
                        message = "NFC Payment of ${paymentData.amount} completed successfully"
                    )
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid payment request"
                        403 -> "Payment not authorized"
                        404 -> "Account not found"
                        422 -> "Insufficient funds"
                        else -> "Payment failed: ${response.code()}"
                    }
                    PaymentResult(
                        success = false,
                        transactionId = null,
                        message = errorMessage
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "NFC Payment failed", e)
                PaymentResult(
                    success = false,
                    transactionId = null,
                    message = "NFC Payment failed: ${e.message}"
                )
            }
        }
    }

    // ============= EXISTING TRANSFER AND TOP-UP METHODS =============

    fun transfer(
        sourceAccount: AccountResponse,
        destinationAccount: AccountResponse,
        amount: BigDecimal,
        onTransactionSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _transferUiState.value = TransferUiState.Loading
            try {
                val request = TransferCreateRequest(
                    sourceAccountNumber = sourceAccount.accountNumber ?: "",
                    destinationAccountNumber = destinationAccount.accountNumber ?: "",
                    amount = amount
                )

                val response = RetrofitInstance.getBankingServiceProvide(context).transfer(request)

                if (response.isSuccessful) {
                    response.body()?.let { transaction ->
                        _transferUiState.value = TransferUiState.Success(transaction)
                        onTransactionSuccess()
                    } ?: run {
                        _transferUiState.value = TransferUiState.Error("Transfer failed: Empty response")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid transfer request"
                        403 -> "Transfer not allowed"
                        404 -> "Account not found"
                        else -> "Transfer failed: ${response.code()}"
                    }
                    _transferUiState.value = TransferUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _transferUiState.value = TransferUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun topUp(amount: BigDecimal) {
        viewModelScope.launch {
            _topUpUiState.value = TopUpUiState.Loading
            try {
                // Simulate top-up logic (external funding)
                // In real implementation, this would call a top-up endpoint
                kotlinx.coroutines.delay(1500) // Simulate API call
                _topUpUiState.value = TopUpUiState.Success("Top-up of ${amount} KWD successful")
            } catch (e: Exception) {
                _topUpUiState.value = TopUpUiState.Error("Top-up failed: ${e.message}")
            }
        }
    }
}

// ============= NFC DATA CLASSES =============

/**
 * Data classes for NFC payment
 */
data class PaymentData(
    val destinationAccount: String,
    val amount: String,
)

data class PaymentResult(
    val success: Boolean,
    val transactionId: String? = null,
    val message: String
)

/**
 * NFC states for UI
 */
sealed class NfcState {
    object Idle : NfcState()
    object Listening : NfcState()
    object Processing : NfcState()
    data class Success(val result: PaymentResult) : NfcState()
    data class Error(val message: String) : NfcState()
}

/**
 * Extension function to convert String to BigDecimal safely
 */
private fun String.toBigDecimalSafe(): BigDecimal {
    return try {
        BigDecimal(this)
    } catch (e: NumberFormatException) {
        BigDecimal.ZERO
    }
}