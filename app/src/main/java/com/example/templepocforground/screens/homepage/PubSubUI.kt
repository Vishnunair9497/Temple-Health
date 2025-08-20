@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.templepocforground.screens.homepage

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.templepocforground.screens.widgetsfun.MessageCard
import com.example.templepocforground.services.PubSubForegroundService
import com.example.templepocforground.services.PubSubMessageStore

@Composable
fun PubSubUI() {
    val context = LocalContext.current
    val messages = PubSubMessageStore.messages
    val connectionState = PubSubMessageStore.connection
    val reestablishSocket = PubSubMessageStore.reestablishSocket
    val viewModel: HomePageViewModel = hiltViewModel()
    var isStopped by rememberSaveable { mutableStateOf(true) }

    fun startConnection() {
        val startIntent = Intent(context, PubSubForegroundService::class.java).apply {
            action = "START_CONNECTION"
        }
        context.startService(startIntent)
        viewModel.setStop(false)
    }

    fun stopConnection() {
        val stopIntent = Intent(context, PubSubForegroundService::class.java).apply {
            action = "STOP_CONNECTION"
        }
        context.startService(stopIntent)
        viewModel.setStop(true)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Azure PubSub Client") }) }
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
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (connectionState.firstOrNull() == "CONNECTED") Color.Green
                                else Color.Red
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Connection status:",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = if (connectionState.firstOrNull() == "CONNECTED") "Connected" else "Disconnected",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    }
                    val isConnected = connectionState.firstOrNull() == "CONNECTED"
                    Switch(
                        checked = isConnected,
                        onCheckedChange = { checked ->
                            isStopped = checked
                            if (checked) startConnection() else stopConnection()
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (messages.isNotEmpty()) {
                    items(messages.size) { index ->
                        MessageCard(messages[index])
                    }
                }
            }
        }
    }
}
