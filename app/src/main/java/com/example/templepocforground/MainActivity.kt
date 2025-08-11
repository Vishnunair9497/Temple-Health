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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
        val context = LocalContext.current
        val messages = PubSubMessageStore.messages
        var isStopped by remember { mutableStateOf(false) }

        if (messages.equals("start")){
            val alertIntent = Intent(context, PubSubForegroundService::class.java)
            alertIntent.action = "ACTION_ALERT"
            context.startService(alertIntent)
        }
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Azure PubSub Client") })
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Last Message:",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = messages.firstOrNull() ?: "No messages yet",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                        }

                        Switch(
                            checked = isStopped,
                            onCheckedChange = { checked ->
                                isStopped = checked
                                if (checked) {
                                    val stopIntent = Intent(context, PubSubForegroundService::class.java).apply {
                                        action = "STOP_ALERT"
                                    }
                                    context.startService(stopIntent)
                                }
                            }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(messages.size) { index ->
                        Text(messages[index])
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }


}
