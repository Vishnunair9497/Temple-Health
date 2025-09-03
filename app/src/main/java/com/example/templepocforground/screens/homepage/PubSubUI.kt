@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.templepocforground.screens.homepage

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.templepocforground.MainActivity
import com.example.templepocforground.R
import com.example.templepocforground.helper.NotificationHelper
import com.example.templepocforground.screens.widgetsfun.CustomListSwitchTile
import com.example.templepocforground.screens.widgetsfun.MessageCard
import com.example.templepocforground.screens.widgetsfun.TraumaAlertPopUp
import com.example.templepocforground.services.PubSubMessageStore
import com.example.templepocforground.utils.getNetworkType
import kotlinx.coroutines.delay


@Composable
fun PubSubUI(homePageViewModel: HomePageViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val messages = PubSubMessageStore.messages
    val connectionState = PubSubMessageStore.connection
    val viewModel: HomePageViewModel = hiltViewModel()
    val networkType = getNetworkType(context)
    val state by homePageViewModel.registerState.collectAsState()
    val onCallState by homePageViewModel.onCallState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var latestMessage by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        viewModel.startConnection()
    }



    LaunchedEffect(Unit) {
        val deviceId = NotificationHelper.getOrCreateAppId(context)
        val fcmToken = NotificationHelper.fetchFcmToken()
        if (fcmToken != null) {
            homePageViewModel.registerDevice(deviceId, fcmToken, context)
        }
    }

    LaunchedEffect(messages.firstOrNull(), state) {
        messages.firstOrNull()?.let { latest ->
            latestMessage = latest.title
            showDialog = true
        }
        state?.onSuccess { response ->
            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
        }?.onFailure { error ->
            Toast.makeText(context, error.message ?: "Error", Toast.LENGTH_SHORT).show()
        }
    }

    if (showDialog && latestMessage != null) {
        messages.firstOrNull()?.let {
            TraumaAlertPopUp(
                title = "New Trauma Alert",
                message = latestMessage ?: "",
                details = it,
                onDismiss = {
                    showDialog = false
                    viewModel.stopSound()
                },
                onConfirm = {
                    viewModel.getSavedUserId()?.let {
                        viewModel.stopAlerts(messages.firstOrNull()?.alertId, it, {
                            showDialog = false
                            viewModel.stopSound()
                        })
                    }

                })

            LaunchedEffect(Unit) {
                delay(30_000L)
                showDialog = false
                viewModel.stopSound()
            }

        }
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        messages.clear()
                        viewModel.setStop(true)
                        viewModel.logOut()
                        viewModel.stopConnection()
                        messages.clear()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.purple_500)
                    )
                ) {
                    Text(
                        text = "Log out", style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ), color = Color.White
                    )
                }
            }
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.temple_background))
                .padding(padding)
        ) {

            viewModel.getSavedUsername()?.let { username ->
                val isConnected = connectionState.firstOrNull() == "CONNECTED"
                CustomListSwitchTile(
                    imageRes = if (isConnected) R.drawable.connectedicon else R.drawable.disconnectedicon,
                    title = username,
                    subtitle = if (isConnected) "Socket : Connected" else "Socket : Disconnected",
                    onClick = {},
                    startConnection = { viewModel.startConnection() },
                    stopConnection = { viewModel.stopConnection() },
                    connectingVia = "Internet : Connecting via $networkType",
                    receivingAlert = if (isConnected) "You are receiving alert" else "Not receiving any alert"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recent Alert",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                ),
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.temple_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val uniqueMessages =
                messages.distinctBy { it.alertId }.sortedByDescending { it.createdDate }
            /*
                        val uniqueMessages = messages
                           .groupBy { it.alertId }
                            .mapValues { entry ->
                                entry.value.maxWithOrNull(
                                    compareBy<AlertResponse> { it.iteration }
                                        .thenByDescending { parseDate(it.createdDate) }
                                )!!
                            }
                            .values
                            .sortedWith(
                                compareByDescending<AlertResponse> { it.iteration }
                                    .thenByDescending { parseDate(it.createdDate) }
                            )*/

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
