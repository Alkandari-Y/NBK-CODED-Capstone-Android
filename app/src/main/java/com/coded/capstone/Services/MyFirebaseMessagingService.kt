package com.coded.capstone.Services

import com.coded.capstone.MainActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.deeplink.DeepLinkHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

// firebase docs reference
// https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/kotlin/MyFirebaseMessagingService.kt

data class NotificationBody (
    val title: String,
    val body: String
)

data class FirebaseToken(
    val token: String
)

class PushNotificationService: FirebaseMessagingService(){
    
    companion object {
        private const val TAG = "PushNotificationService"
        private const val CHANNEL_ID = "nbk_capstone_channel"
        private const val CHANNEL_NAME = "NBK Capstone Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for NBK Capstone app"
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // Send token to backend
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Handle notification payload if needed
        }
    }
    
    /**
     * Handle FCM data messages with deep link information
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Notification"
        val body = data["body"] ?: ""
        val deepLink = data["deepLink"]
        val targetScreen = data["targetScreen"]
        val requiresAuth = data["requiresAuth"]?.toBoolean() ?: true
        val parameters = data["parameters"]
        
        Log.d(TAG, "Processing data message - DeepLink: $deepLink, TargetScreen: $targetScreen")
        
        // Create notification with deep link intent
        createNotificationWithDeepLink(title, body, deepLink, targetScreen, parameters)
        
        // Store deep link for navigation if app is in foreground
        if (deepLink != null && targetScreen != null) {
            storeDeepLinkForNavigation(deepLink, targetScreen, parameters)
        }
    }
    
    /**
     * Create notification with deep link intent
     */
    private fun createNotificationWithDeepLink(
        title: String,
        body: String,
        deepLink: String?,
        targetScreen: String?,
        parameters: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent for notification tap
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (deepLink != null) {
                data = android.net.Uri.parse(deepLink)
                putExtra("deepLink", deepLink)
                putExtra("targetScreen", targetScreen)
                putExtra("parameters", parameters)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        
        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    /**
     * Store deep link for navigation when app is in foreground
     */
    private fun storeDeepLinkForNavigation(
        deepLink: String,
        targetScreen: String,
        parameters: String?
    ) {
        val prefs = getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("pending_deeplink", deepLink)
            putString("pending_target_screen", targetScreen)
            putString("pending_parameters", parameters)
            putLong("pending_timestamp", System.currentTimeMillis())
        }.apply()
        
        Log.d(TAG, "Stored pending deep link: $deepLink")
    }
    
    /**
     * Send FCM token to backend server
     */
    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // TODO: Implement token sending to your backend
                // This should call your backend API to register the FCM token
                Log.d(TAG, "Token sent to server: $token")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send token to server", e)
            }
        }
    }
}
