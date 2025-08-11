package com.example.templepocforground.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.templepocforground.R
import com.example.templepocforground.helper.NotificationHelper
import com.example.templepocforground.helper.PubSubServiceActionEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class PubSubForegroundService : Service() {
    private lateinit var webSocket: WebSocket
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startWebSocket()
        NotificationHelper.createChannels(this)
        startForeground(1, NotificationHelper.createRunningNotification(this))
    }

    private fun startForegroundService() {
        val channelId = "azure_pubsub_channel"
        val channelName = "Azure PubSub Background"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat.Builder(this, channelId).setContentTitle("Azure PubSub Running")
                .setContentText("Listening for messages in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground).build()

        startForeground(1, notification)
    }

    private fun startWebSocket() {
        val tokenUrl =

            "wss://mypubsubdemo.webpubsub.azure.com/client/hubs/hub?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE3NTQ5MDMyOTQsImV4cCI6MTc1NDk4OTY5NCwiaWF0IjoxNzU0OTAzMjk0LCJhdWQiOiJodHRwczovL215cHVic3ViZGVtby53ZWJwdWJzdWIuYXp1cmUuY29tL2NsaWVudC9odWJzL2h1YiJ9.D_7rl1J-YA4fqBbtPARsxIXRvy3uOF0yv6oXC762nUk"

        val client = OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).pingInterval(
            30, TimeUnit.SECONDS
        ).build()
        val request = Request.Builder().url(tokenUrl).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("AzurePubSub", "Connected")
                PubSubMessageStore.addMessage("Text: ${response.message}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("AzurePubSub", "Message: $text")
                PubSubMessageStore.addMessage("Text: $text")
                CoroutineScope(Dispatchers.Main).launch {
                    // SoundEventNotifier.notifyPlaySound()
                    when (text.trim().lowercase()) {
                        "start" -> playAlertSound()
                        "stop" -> stopSound()
                        else -> Log.d("AzurePubSub", "Unknown command: $text")
                    }

                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("AzurePubSub", "Binary Message: ${bytes.hex()}")
                PubSubMessageStore.addMessage("Binary: ${bytes.hex()}")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("AzurePubSub", "WebSocket Error: ${t.message}")
                PubSubMessageStore.addMessage("Error: ${t.message}")
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    startWebSocket()
                }
            }
        }

        webSocket = client.newWebSocket(request, listener)

    }


    private fun playAlertSound() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Toast.makeText(this, "Audio focus not granted", Toast.LENGTH_SHORT).show()
            vibrateFallback()
            return
        }
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, 4, AudioManager.FLAG_SHOW_UI
        )

        /*audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0,
        )*/

        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound)
        if (mediaPlayer == null) {
            vibrateFallback()
            return
        }

        mediaPlayer?.apply {
            isLooping = false
            setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
            start()
        }

        Toast.makeText(this, "Playing alert sound...", Toast.LENGTH_SHORT).show()
    }

    private fun vibrateFallback() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
        Log.d("TempleHealth", "Vibration fallback triggered.")
    }


    private fun stopSound() {
        Log.d("STOPPED", "stopSound: ")
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "STOP_ALERT" -> stopSound()

            "ACTION_ALERT" -> {
                val notification = NotificationHelper.createAlertNotification(this)
                val manager = getSystemService(NotificationManager::class.java)
                manager.notify(2, notification)
            }
            "ACTION_CLOSE" -> stopSelf()
            "ACTION_MUTE" -> {
                // no mute need to add here // vishnu
            }
        }
        return START_STICKY
    }

    /*  private fun getNewToken(callback: (String) -> Unit) {
          val client = OkHttpClient()
          val request = Request.Builder()
              .url("https://your-api-endpoint.com/getToken")
              .build()

          client.newCall(request).enqueue(object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                  Log.e("TokenFetch", "Failed to get token: ${e.message}")
              }

              override fun onResponse(call: Call, response: Response) {
                  if (response.isSuccessful) {
                      val token = response.body?.string() ?: ""
                      callback(token)
                  } else {
                      Log.e("TokenFetch", "Token API error: ${response.code}")
                  }
              }
          })
      }*/


}