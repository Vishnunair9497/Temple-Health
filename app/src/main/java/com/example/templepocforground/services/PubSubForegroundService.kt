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
import androidx.core.app.NotificationCompat
import com.example.templepocforground.R
import com.example.templepocforground.helper.NotificationHelper
import com.example.templepocforground.utils.NetworkMonitor
import com.example.templepocforground.utils.SharedPrefsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class PubSubForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "azure_pubsub_channel"
        private const val CHANNEL_NAME = "Azure PubSub Background"
        private const val NOTIFICATION_ID = 1

        const val ACTION_STOP_ALERT = "STOP_ALERT"
        const val ACTION_ALERT = "ACTION_ALERT"
        const val ACTION_STOP_CONNECTION = "STOP_CONNECTION"
        const val ACTION_START_CONNECTION = "START_CONNECTION"
        const val ACTION_MUTE = "ACTION_MUTE"
    }

    @Inject
    lateinit var prefsManager: SharedPrefsManager

    private var tokenUrl: String? = null
    private lateinit var webSocket: WebSocket
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var networkMonitor: NetworkMonitor
    private var retryCount = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        networkMonitor = NetworkMonitor(this)
        initForegroundService()
        callWebSocket()
        NotificationHelper.createChannels(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSound()
        scope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_ALERT -> stopSound()
            ACTION_ALERT -> notifyAlert()
            ACTION_STOP_CONNECTION -> stopConnection()
            ACTION_START_CONNECTION -> if (!prefsManager.isStopped()) startWebSocket()
            ACTION_MUTE -> {}
        }
        return START_NOT_STICKY
    }

    private fun initForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Azure PubSub Running")
                .setContentText("Listening for messages in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground).build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun notifyAlert() {
        val notification = NotificationHelper.createAlertNotification(this)
        getSystemService(NotificationManager::class.java).notify(2, notification)
    }

    private fun callWebSocket() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                networkMonitor.isConnected.collect { connected ->
                    try {
                        if (connected) {
                            val url = prefsManager.getSocketUrl()
                            if (!url.isNullOrEmpty()) {
                                tokenUrl = url
                                initForegroundService()
                                startWebSocket()
                            } else {
                                Log.w("WebSocket", "TokenUrl not available yet, waiting...")
                                waitForTokenUrl()
                            }
                        } else {
                            Log.e("WebSocket", "No internet connection.")
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "Error inside collect block: ${e.message}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error starting callWebSocket: ${e.message}", e)
            }
        }
    }


    private suspend fun waitForTokenUrl() {
        withContext(Dispatchers.IO) {
            var url: String? = null
            while (url.isNullOrEmpty()) {
                url = prefsManager.getSocketUrl()
                delay(1000)
            }
            tokenUrl = url
            withContext(Dispatchers.Main) {
                initForegroundService()
                startWebSocket()
            }
        }
    }

    private fun startWebSocket() {
        stopSound()
        val url = tokenUrl ?: return
        val client = OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS)
            .pingInterval(10, TimeUnit.SECONDS).build()

        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                if (!prefsManager.isStopped()) {
                    PubSubMessageStore.connectionStatus("CONNECTED")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (prefsManager.isStopped() || prefsManager.isLogOut() == true) return

                Log.e("Message >>>>", "Text >>>> $text")
                PubSubMessageStore.addMessage(text)
                PubSubMessageStore.connectionStatus("CONNECTED")

                CoroutineScope(Dispatchers.Main).launch {
                    val latestMessage = PubSubMessageStore.messages.firstOrNull()
                    latestMessage?.data?.let {
                        if (stopSoundSafely()) {
                            playAlertSound(it.Category)
                        }
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("PubSubService", "Binary Message: ${bytes.hex()}")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("PubSubService", "WebSocket failure: ${t.message}")
                PubSubMessageStore.connectionStatus("CONNECTION_FAIL")
                scope.launch {
                    delay(2000)
                    if (!prefsManager.isStopped()) {
                        if (retryCount < 5) {
                            retryCount++
                            callWebSocket()
                        } else {
                            PubSubMessageStore.triggerReestablishSocket()
                            callWebSocket()
                            retryCount = 0
                        }
                    }
                }
            }
        })
    }

    private fun stopConnection() {
        if (::webSocket.isInitialized) {
            try {
                webSocket.close(1000, "Service stopped by user")
                Log.d("PubSubService", "WebSocket closed.")
            } catch (e: Exception) {
                Log.e("PubSubService", "Error closing WebSocket: ${e.message}")
            }
        }
        prefsManager.setStopped(true)
        stopSound()
        stopSoundSafely()
        mediaPlayer?.release()
        stopSelf()
        PubSubMessageStore.connectionStatus("CONNECTION_FAIL")
        onDestroy()
    }

    private fun stopSound() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
            mediaPlayer = null
        }
    }

    private fun stopSoundSafely(): Boolean {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
                mediaPlayer = null
                return true
            } catch (e: Exception) {
                Log.e("stopSoundSafely", "Error stopping sound: ${e.message}")
                return false
            }
        }
        return true
    }

    private fun playAlertSound(category: String) {
        stopSound()

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val focusGranted = audioManager.requestAudioFocus(
            null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )

        if (focusGranted != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            vibrateFallback()
            return
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_PLAY_SOUND)

        val alertSound: Int? = when (category.lowercase()) {
            "cat1" -> R.raw.alerttwo
            "cat2" -> R.raw.alertone
            "cat3" -> R.raw.alertthree
            else -> {
                Log.e("playAlertSound", "Unknown category: $category")
                null
            }
        }

        if (alertSound != null) {
            mediaPlayer = MediaPlayer.create(this, alertSound)
            mediaPlayer?.apply {
                isLooping = false
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    audioManager.abandonAudioFocus(null)
                }
                start()
            }
        } else {
            vibrateFallback()
        }
    }

    private fun vibrateFallback() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION") vibrator.vibrate(500)
        }
        Log.d("PubSubService", "Vibration fallback triggered.")
    }
}

