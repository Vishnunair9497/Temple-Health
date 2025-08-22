@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.templepocforground.screens.homepage

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.templepocforground.R
import com.example.templepocforground.screens.widgetsfun.CustomListSwitchTile
import com.example.templepocforground.screens.widgetsfun.MessageCard
import com.example.templepocforground.screens.widgetsfun.TraumaAlertPopUp
import com.example.templepocforground.services.PubSubForegroundService
import com.example.templepocforground.services.PubSubMessageStore
import com.example.templepocforground.utils.getNetworkType


@Composable
fun PubSubUI() {
    val context = LocalContext.current
    val messages = PubSubMessageStore.messages
    val connectionState = PubSubMessageStore.connection
    val viewModel: HomePageViewModel = hiltViewModel()
    val networkType = getNetworkType(context)


    var showDialog by remember { mutableStateOf(false) }
    var latestMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(messages.firstOrNull()) {
        messages.firstOrNull()?.let { latest ->
            latestMessage = latest.Message
            showDialog = true
        }
    }

    if (showDialog && latestMessage != null) {
        TraumaAlertPopUp (
            title = "New Trauma Alert",
            message = latestMessage ?: "",
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
            }
        )
    }

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
        // topBar = { TopAppBar(title = { Text("Azure PubSub Client") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.temple_background))
                .padding(padding)
        ) {

            viewModel.getSavedUsername()?.let { username ->
                val isConnected = connectionState.firstOrNull() == "CONNECTED"
                CustomListSwitchTile(
                    imageRes = if (isConnected) R.drawable.connectedpersonicon else R.drawable.disconnectedpersonicon,
                    title = username,
                    subtitle = if (isConnected) "Connected" else "Disconnected",
                    onClick = {},
                    startConnection = { startConnection() },
                    stopConnection = { stopConnection() },
                    connectingVia = "Connecting via $networkType",
                    receivingAlert = if (isConnected) "You are receiving alert" else "Not receiving any alert"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recent Alert",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.temple_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val uniqueMessages = messages.distinctBy { it.Id }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (uniqueMessages.isNotEmpty()) {
                    items(uniqueMessages.size) { index ->
                        MessageCard(uniqueMessages[index])
                    }
                }
            }
        }
    }
}
