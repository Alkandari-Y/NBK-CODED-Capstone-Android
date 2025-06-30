package com.coded.capstone.services

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import com.coded.capstone.providers.RetrofitInstance

class NfcPaymentService(private val context: android.content.Context) {
    companion object {
        private const val TAG = "NfcPaymentService"
        private const val BASE_URL = "http://localhost:8001/"
        
        // EMV APDU Commands
        private val SELECT_PPSE = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
            0x0E.toByte(), 0x32.toByte(), 0x50.toByte(), 0x41.toByte(),
            0x59.toByte(), 0x2E.toByte(), 0x53.toByte(), 0x59.toByte(),
            0x53.toByte(), 0x2E.toByte(), 0x44.toByte(), 0x44.toByte(),
            0x46.toByte(), 0x30.toByte(), 0x31.toByte()
        )
        
        private val SELECT_AID = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
            0x07.toByte(), 0xA0.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x03.toByte(), 0x10.toByte(), 0x10.toByte()
        )
        
        private val GET_PROCESSING_OPTIONS = byteArrayOf(
            0x80.toByte(), 0xA8.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x02.toByte(), 0x83.toByte(), 0x00.toByte()
        )
        
        private val READ_RECORD = byteArrayOf(
            0x00.toByte(), 0xB2.toByte(), 0x01.toByte(), 0x0C.toByte(),
            0x00.toByte()
        )
    }
    
    interface NfcPaymentCallback {
        fun onPaymentStarted()
        fun onPaymentSuccess(transactionId: String)
        fun onPaymentFailed(error: String)
        fun onNfcNotAvailable()
        fun onNfcNotEnabled()
        fun onCardDataRead(destinationAccount: String, amount: BigDecimal)
    }
    
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null
    
    // Use shared Retrofit instance with TokenInterceptor
    private val bankingApiService by lazy {
        RetrofitInstance.getBankingServiceProvide(context)
    }
    
    fun initialize(activity: Activity): Boolean {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        
        if (nfcAdapter == null) {
            Log.e(TAG, "NFC is not available on this device")
            return false
        }
        
        if (!nfcAdapter!!.isEnabled) {
            Log.e(TAG, "NFC is not enabled")
            return false
        }
        
        // Create PendingIntent for NFC discovery
        val intent = Intent(activity, activity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        pendingIntent = PendingIntent.getActivity(activity, 0, intent, flags)
        
        // Create intent filters for NFC discovery
        val ndef = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Failed to set MIME type", e)
            }
        }
        
        intentFilters = arrayOf(ndef)
        
        // Create tech lists
        techLists = arrayOf(
            arrayOf(android.nfc.tech.IsoDep::class.java.name),
            arrayOf(android.nfc.tech.NfcA::class.java.name),
            arrayOf(android.nfc.tech.NfcB::class.java.name),
            arrayOf(android.nfc.tech.NfcF::class.java.name),
            arrayOf(android.nfc.tech.NfcV::class.java.name),
            arrayOf(android.nfc.tech.Ndef::class.java.name)
        )
        
        return true
    }
    
    fun enableForegroundDispatch(activity: Activity) {
        nfcAdapter?.enableForegroundDispatch(
            activity,
            pendingIntent,
            intentFilters,
            techLists
        )
    }
    
    fun disableForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }
    
    suspend fun processPayment(tag: Tag, sourceAccountNumber: String, callback: NfcPaymentCallback): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                callback.onPaymentStarted()
                
                // Log available technologies for debugging
                val techList = tag.techList
                Log.d(TAG, "Available NFC technologies: ${techList.joinToString(", ")}")
                
                // First, try to read NDEF data from the card
                val cardData = readNdefData(tag)
                if (cardData != null) {
                    Log.d(TAG, "Successfully read NDEF data: destination=${cardData.destinationAccount}, amount=${cardData.amount}")
                    callback.onCardDataRead(cardData.destinationAccount, cardData.amount)
                    
                    // Make payment request to backend
                    val success = makePaymentRequest(sourceAccountNumber, cardData.destinationAccount, cardData.amount)
                    
                    if (success) {
                        val transactionId = "TXN${System.currentTimeMillis()}"
                        callback.onPaymentSuccess(transactionId)
                        true
                    } else {
                        callback.onPaymentFailed("Payment request failed")
                        false
                    }
                } else {
                    // Fallback to EMV simulation if NDEF data not found
                    Log.d(TAG, "No NDEF data found, falling back to EMV simulation")
                    val isoDep = IsoDep.get(tag)
                    if (isoDep != null) {
                        isoDep.connect()
                        // Simulate EMV payment flow
                        val transactionId = simulateEmvPayment(isoDep, BigDecimal("10.00"))
                        isoDep.close()
                        // Simulate network delay
                        kotlinx.coroutines.delay(2000)
                        callback.onPaymentSuccess(transactionId)
                        true
                    } else {
                        // Check what technologies are available and provide specific error
                        val availableTechs = tag.techList.joinToString(", ")
                        Log.e(TAG, "IsoDep is not supported by this tag. Available technologies: $availableTechs")
                        
                        val errorMessage = when {
                            availableTechs.contains("android.nfc.tech.Ndef") -> 
                                "This NFC tag contains data but no payment information. Please use a payment card or tag with payment data."
                            availableTechs.contains("android.nfc.tech.NfcA") || 
                            availableTechs.contains("android.nfc.tech.NfcB") || 
                            availableTechs.contains("android.nfc.tech.NfcF") || 
                            availableTechs.contains("android.nfc.tech.NfcV") -> 
                                "This NFC tag is not a payment card. Please use a credit/debit card or payment-enabled device."
                            else -> 
                                "This NFC card is not supported for payments. Please use a different payment method."
                        }
                        
                        callback.onPaymentFailed(errorMessage)
                        false
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Payment processing failed", e)
                callback.onPaymentFailed("Payment failed: ${e.message}")
                false
            }
        }
    }
    
    private fun readNdefData(tag: Tag): CardData? {
        try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                if (ndefMessage != null) {
                    for (record in ndefMessage.records) {
                        // Support MIME type 'text/plain' records
                        if (record.toMimeType() == "text/plain") {
                            val payload = record.payload
                            val text = String(payload, StandardCharsets.UTF_8)
                            // Parse the text to extract destination account and amount
                            val lines = text.split("\n")
                            var destinationAccount: String? = null
                            var amount: BigDecimal? = null
                            for (line in lines) {
                                when {
                                    line.startsWith("destination_account:") -> {
                                        destinationAccount = line.substringAfter("destination_account:").trim()
                                    }
                                    line.startsWith("amount:") -> {
                                        val amountStr = line.substringAfter("amount:").trim()
                                        try {
                                            amount = BigDecimal(amountStr)
                                        } catch (e: NumberFormatException) {
                                            Log.e(TAG, "Invalid amount format: $amountStr")
                                        }
                                    }
                                }
                            }
                            if (destinationAccount != null && amount != null) {
                                ndef.close()
                                return CardData(destinationAccount, amount)
                            }
                        }
                        // Support 'Well Known' Text (type 'T') records
                        if (record.tnf == android.nfc.NdefRecord.TNF_WELL_KNOWN &&
                            record.type.contentEquals(android.nfc.NdefRecord.RTD_TEXT)) {
                            val payload = record.payload
                            // NDEF Text Record: [status byte][language code][text]
                            val status = payload[0].toInt() and 0xFF
                            val langLength = status and 0x3F
                            val text = String(payload, 1 + langLength, payload.size - 1 - langLength, StandardCharsets.UTF_8)
                            // Parse the text to extract destination account and amount
                            val lines = text.split("\n")
                            var destinationAccount: String? = null
                            var amount: BigDecimal? = null
                            for (line in lines) {
                                when {
                                    line.startsWith("destination_account:") -> {
                                        destinationAccount = line.substringAfter("destination_account:").trim()
                                    }
                                    line.startsWith("amount:") -> {
                                        val amountStr = line.substringAfter("amount:").trim()
                                        try {
                                            amount = BigDecimal(amountStr)
                                        } catch (e: NumberFormatException) {
                                            Log.e(TAG, "Invalid amount format: $amountStr")
                                        }
                                    }
                                }
                            }
                            if (destinationAccount != null && amount != null) {
                                ndef.close()
                                return CardData(destinationAccount, amount)
                            }
                        }
                    }
                }
                ndef.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading NDEF data", e)
        }
        return null
    }
    
    private suspend fun makePaymentRequest(sourceAccountNumber: String, destinationAccountNumber: String, amount: BigDecimal): Boolean {
        return try {
            // Create payment request DTO
            val paymentRequest = PaymentCreateRequest(
                sourceAccountNumber = sourceAccountNumber,
                destinationAccountNumber = destinationAccountNumber,
                amount = amount,
                type = TransactionType.PAYMENT
            )
            
            // Make HTTP request to backend
            val response = bankingApiService.makePurchase(paymentRequest)
            
            Log.d(TAG, "Payment request response: ${response.code()} - ${response.message()}")
            
            if (response.isSuccessful) {
                val paymentResponse = response.body()
                Log.d(TAG, "Payment response: $paymentResponse")
                paymentResponse?.success == true
            } else {
                Log.e(TAG, "Payment request failed: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error making payment request", e)
            false
        }
    }
    
    private fun simulateEmvPayment(isoDep: IsoDep, amount: BigDecimal): String {
        // Simulate EMV payment flow
        Log.d(TAG, "Starting EMV payment simulation for amount: $amount")
        
        // Step 1: Select PPSE
        val ppseResponse = isoDep.transceive(SELECT_PPSE)
        Log.d(TAG, "PPSE Response: ${ppseResponse.toHexString()}")
        
        // Step 2: Select AID
        val aidResponse = isoDep.transceive(SELECT_AID)
        Log.d(TAG, "AID Response: ${aidResponse.toHexString()}")
        
        // Step 3: Get Processing Options
        val gpoResponse = isoDep.transceive(GET_PROCESSING_OPTIONS)
        Log.d(TAG, "GPO Response: ${gpoResponse.toHexString()}")
        
        // Step 4: Read Records
        val recordResponse = isoDep.transceive(READ_RECORD)
        Log.d(TAG, "Record Response: ${recordResponse.toHexString()}")
        
        // Generate transaction ID
        val transactionId = "TXN${System.currentTimeMillis()}"
        Log.d(TAG, "Payment completed successfully. Transaction ID: $transactionId")
        
        return transactionId
    }
    
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }
    
    fun isNfcAvailable(): Boolean {
        return nfcAdapter != null
    }
    
    fun isNfcEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }
    
    // Data class for card data
    data class CardData(
        val destinationAccount: String,
        val amount: BigDecimal
    )
    
    private fun getBaseUrl(port: Int): String = "http://192.168.22.54:$port/"
} 