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

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token generated: $token")

        val prefs = applicationContext.getSharedPreferences("firebase_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("firebase_token", token).apply()
        Log.d(TAG, "Firebase token stored directly in SharedPreferences.")

        CoroutineScope(Dispatchers.IO).launch {
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Push notification received from: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "New Notification"
            val body = notification.body ?: "You have a new message."

            Log.d(TAG, "Notification Title: $title")
            Log.d(TAG, "Notification Body: $body")

            showNotification(title, body)
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "geofence_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, notificationBuilder.build())
    }
}

data class NotificationBody (
    val title: String,
    val body: String
)

data class FirebaseToken(
    val token: String
)