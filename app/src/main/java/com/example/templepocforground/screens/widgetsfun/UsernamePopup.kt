package com.example.templepocforground.screens.widgetsfun

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.templepocforground.screens.homepage.HomePageViewModel

@Composable
fun UsernamePopup(context: Context, onSave: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    val viewModel: HomePageViewModel = hiltViewModel()

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Enter Username") },
        text = {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                if (username.isNotBlank()) {
                    viewModel.saveUsername( username)
                    viewModel.getSocketUrl(username) {
                        viewModel.setStop(false)
                        onSave(username)
                    }
                }
            }) {
                Text("Login")
            }
        },
        dismissButton = {}
    )
}
