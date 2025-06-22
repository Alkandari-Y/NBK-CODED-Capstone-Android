package com.coded.capstone.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.coded.capstone.MainActivity
import com.coded.capstone.R
// import com.coded.capstone.managers.ServiceFlowManager // No longer needed
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebasePushNotificationService : FirebaseMessagingService() {

    private val TAG = "FirebasePushService"

    /**
     * Called when a new Firebase Cloud Messaging token is generated.
     * This token is the unique identifier for this app instance, used to send
     * push notifications to this specific device.
     *
     * @param token The new token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token generated: $token")

        // Store the new token locally using SharedPreferences directly
        val prefs = applicationContext.getSharedPreferences("firebase_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("firebase_token", token).apply()
        Log.d(TAG, "Firebase token stored directly in SharedPreferences.")

        // If needed, you can also send this token to your backend immediately.
        // This is important for ensuring the backend always has the latest token.
        CoroutineScope(Dispatchers.IO).launch {
            // Here you could add a call to your backend to update the token
            // e.g., YourApiManager.updateToken(applicationContext, token)
        }
    }

    /**
     * Called when a new push notification message is received from Firebase.
     * This is where you handle the incoming notification and display it to the user.
     *
     * @param remoteMessage The message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Push notification received from: ${remoteMessage.from}")

        // Check if the message contains a notification payload.
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "New Notification"
            val body = notification.body ?: "You have a new message."

            Log.d(TAG, "Notification Title: $title")
            Log.d(TAG, "Notification Body: $body")

            // Show the notification to the user
            showNotification(title, body)
        }
    }

    /**
     * Creates and displays a system notification on the device.
     *
     * @param title The title of the notification.
     * @param body The main content/body of the notification.
     */
    private fun showNotification(title: String, body: String) {
        val channelId = "geofence_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android O and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for geofence events and offers"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to open the app when the notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        // Display the notification
        notificationManager.notify(0, notificationBuilder.build())
    }
} 