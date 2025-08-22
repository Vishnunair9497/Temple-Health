package com.example.templepocforground.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.templepocforground.MainActivity
import com.example.templepocforground.R
import com.example.templepocforground.services.PubSubForegroundService
import constants.Constants

object NotificationHelper {

    const val ALERT_CHANNEL_ID = "alert_channel"
    const val RUNNING_CHANNEL_ID = "azure_pubsub_channel"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val runningChannel = NotificationChannel(
                RUNNING_CHANNEL_ID,
                "Azure PubSub Background",
                NotificationManager.IMPORTANCE_LOW
            )

            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Alert Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(runningChannel)
            manager.createNotificationChannel(alertChannel)
        }
    }

    fun createRunningNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, RUNNING_CHANNEL_ID)
            .setContentTitle("Azure PubSub Running")
            .setContentText("Listening for messages in background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    fun createAlertNotification(context: Context): Notification {
        val openIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val closeIntent = PendingIntent.getService(
            context, 0,
            Intent(context, PubSubForegroundService::class.java).apply {
                action = "ACTION_CLOSE"
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val muteIntent = PendingIntent.getService(
            context, 0,
            Intent(context, PubSubForegroundService::class.java).apply {
                action = "ACTION_MUTE"
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("Alert Received")
            .setContentText("You have a new alert")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(0, "Open", openIntent)
            .addAction(0, "Close", closeIntent)
            .addAction(0, "Mute", muteIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    fun showPushNotification(context: Context, title: String, message: String) {
        val channelId = Constants.CHANNEL_ID
        val channelName = Constants.CHANNEL_NAME

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = Constants.CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Pre-Oreo
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

}
