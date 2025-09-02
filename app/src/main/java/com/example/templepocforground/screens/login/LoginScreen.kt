package com.example.templepocforground.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.templepocforground.R
import com.example.templepocforground.models.authenticate
import com.example.templepocforground.screens.homepage.HomePageViewModel
import com.example.templepocforground.utils.NetworkMonitor


@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    networkMonitor: NetworkMonitor = NetworkMonitor(LocalContext.current)

) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(true) }

    val viewModel: HomePageViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        networkMonitor.isConnected.collect { connected ->
            isConnected = connected
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Critical Alert",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.temple_text),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.logindocimage),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username.trim(),
                onValueChange = { username = it.trim() },
                label = { Text("Username") },
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password.trim(),
                onValueChange = { password = it.trim() },
                label = { Text("Password") },
                textStyle = TextStyle(color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(46.dp))
        Button(
            onClick = {
                when {
                    !isConnected -> {
                        Toast.makeText(
                            context,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    username.isBlank() || password.isBlank() -> {
                        Toast.makeText(
                            context,
                            "Username and Password cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    username.length < 3 || password.length < 3 -> {
                        Toast.makeText(
                            context,
                            "Username and Password must be at least 3 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        val result = authenticate(username, password)
                        if (result != null) {
                            viewModel.getSocketUrl(result) {
                                viewModel.saveUsername(username)
                                viewModel.saveUserId(result)
                                viewModel.setStop(false)
                                onLoginClick(username, password)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Please try valid credentials",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .weight(1f, fill = false),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.temple_text),
                contentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Login/Authenticate",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    //painter = painterResource(id = R.drawable.loginarrow),
                    contentDescription = "Arrow",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }

}