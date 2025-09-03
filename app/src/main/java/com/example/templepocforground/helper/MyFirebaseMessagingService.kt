package com.example.templepocforground.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.templepocforground.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "fcm_channel"
        private const val GROUP_KEY_FCM = "com.example.fcm.GROUP"
        private const val SUMMARY_ID = 0 // summary fixed ID
        private val pendingMessages = mutableListOf<String>()
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "No Title"
        val body = message.notification?.body ?: "No Body"

        showNotification(title, body)
    }


    private fun showNotification(title: String, message: String) {
        val channelId = "fcm_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 1. Store message
        pendingMessages.add("$title: $message")

        // 2. Create child notification
        val childNotification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup("GROUP_FCM")
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), childNotification)

        // 3. Build summary notification
        val inboxStyle = NotificationCompat.InboxStyle()
        pendingMessages.forEach { inboxStyle.addLine(it) }
        inboxStyle.setSummaryText("${pendingMessages.size} new messages")

        val summaryNotification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Messages")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setStyle(inboxStyle)
            .setGroup("GROUP_FCM")
            .setGroupSummary(true)
            // .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, summaryNotification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to your server if needed
    }
}
