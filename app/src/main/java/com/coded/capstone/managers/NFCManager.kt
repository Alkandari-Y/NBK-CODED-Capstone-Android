package com.coded.capstone.managers

import android.annotation.SuppressLint
import android.nfc.Tag
import com.coded.capstone.viewModels.TransactionViewModel

object NFCManager {
    @SuppressLint("StaticFieldLeak")
    private var transactionViewModel: TransactionViewModel? = null

    fun setTransactionViewModel(viewModel: TransactionViewModel) {
        transactionViewModel = viewModel
    }

    fun processNfcTag(tag: Tag) {
        transactionViewModel?.processNfcTag(tag)
    }

    fun clearViewModel() {
        transactionViewModel = null
    }
}