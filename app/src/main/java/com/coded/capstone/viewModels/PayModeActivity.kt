package com.coded.capstone.viewModels

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NdefMessage
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.requests.payment.PaymentCreateRequest
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.respositories.AccountRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class PayModeActivity(
    private val context: Context,
    private val homeScreenViewModel: HomeScreenViewModel
) : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pay_mode)
//
//        statusText = findViewById(R.id.statusText)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            statusText.text = "NFC not supported"
        } else if (!nfcAdapter.isEnabled) {
            statusText.text = "NFC is disabled"
        } else {
            statusText.text = "Ready to Tap"
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        val techListsArray = arrayOf<Array<String>>()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techListsArray)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                val msgs = rawMsgs.map { it as NdefMessage }
                for (msg in msgs) {
                    for (record in msg.records) {
                        val payload = record.payload
                        val payloadText = String(payload).drop(3) // Drop language code bytes
                        homeScreenViewModel.handleNfcPayload(payloadText)
                    }
                }
            }
        }
    }
}