package com.coded.capstone

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Custom Application class to handle one-time initializations.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase when the application starts
        FirebaseApp.initializeApp(this)
    }
} 