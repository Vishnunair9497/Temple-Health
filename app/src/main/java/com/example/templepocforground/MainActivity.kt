package com.example.templepocforground

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.templepocforground.services.PubSubForegroundService
import com.example.templepocforground.services.PubSubMessageStore
import com.example.templepocforground.ui.theme.TemplePOCForgroundTheme

class MainActivity : ComponentActivity() {
    private val foregroundPermission = Manifest.permission.FOREGROUND_SERVICE
    private val permissionRequestCode = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestForegroundServicePermission()
        val serviceIntent = Intent(this, PubSubForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        enableEdgeToEdge()
        setContent {
            TemplePOCForgroundTheme {
                PubSubUI()
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestForegroundServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(foregroundPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(foregroundPermission),
                    permissionRequestCode
                )
            }
        }
        Log.d("Resquest Aceess", "requestForegroundServicePermission: granded ")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PubSubUI() {
        val messages = PubSubMessageStore.messages
        Log.d("Message", ">>>>: $messages ")

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Azure PubSub Client") })
            }
        ) { padding ->
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)) {
                items(messages.size) {
                    Text(messages[it])
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

}
