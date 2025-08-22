package com.example.templepocforground

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.templepocforground.screens.homepage.HomePageViewModel
import com.example.templepocforground.screens.homepage.PubSubUI
import com.example.templepocforground.screens.login.LoginScreen
import com.example.templepocforground.services.PubSubForegroundService
import com.example.templepocforground.services.PubSubMessageStore
import com.example.templepocforground.ui.theme.TemplePOCForgroundTheme
import com.example.templepocforground.utils.NetworkMonitor
import com.example.templepocforground.utils.NoInternetDialog
import com.example.templepocforground.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val foregroundPermission = Manifest.permission.FOREGROUND_SERVICE
    private val permissionRequestCode = 1001
    private val viewModel: HomePageViewModel by viewModels()
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestForegroundServicePermission()
        networkMonitor = NetworkMonitor(this)

        if (!viewModel.getSavedSocketUrl().isNullOrEmpty()) {
            Log.e("TAG", "onCreate: ${viewModel.getSavedSocketUrl()}")
            val serviceIntent = Intent(this, PubSubForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                PubSubMessageStore.reestablishSocket.collect { shouldReestablish ->
                    if (shouldReestablish){
                        viewModel.getSavedUserId()?.let {
                            viewModel.getSocketUrl(it) {
                                viewModel.setStop(false)
                                PubSubMessageStore.resetReestablishSocket()
                                viewModel.getSavedSocketUrl()
                                    ?.let { msg -> Log.e("Reinitiate >>>>>", msg) }
                            }
                        }
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            TemplePOCForgroundTheme {
                val viewModel: HomePageViewModel = hiltViewModel()
                val context = LocalContext.current
                var showPopup by remember {
                    mutableStateOf(
                        viewModel.getSavedUsername().isNullOrEmpty()
                    )
                }
                var isConnected by remember { mutableStateOf(true) }
                val state = viewModel.socketState
                LaunchedEffect(Unit) {
                    networkMonitor.isConnected.collect { connected ->
                        isConnected = connected
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    if (showPopup) {
                        LoginScreen { username,password ->
                            showPopup = false
                        }
                       /* UsernamePopup(context) { enteredUsername ->
                            showPopup = false
                        }*/
                    } else {
                        if (!viewModel.getSavedSocketUrl().isNullOrEmpty()) {
                            PubSubUI()
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                }
                NoInternetDialog(isVisible = !isConnected)
                when (state) {
                    is Resource.Loading -> CircularProgressIndicator()
                    is Resource.Error -> Text("Error: ${state.message}", color = Color.Red)
                    else -> {}
                }


                /*   val navController = rememberNavController()
                   NavHost(navController, startDestination = "login") {
                       composable("login") {
                           LoginScreen { navController.navigate("main") }
                       }
                       composable("register") {}
                       composable("main") {
                           PubSubUI()
                       }
                   }*/
            }
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun requestForegroundServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(foregroundPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(foregroundPermission), permissionRequestCode
                )
            }
        }

        Log.d("RequestAccess", "requestForegroundServicePermission: checked")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
        // TODO: runtime permission 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

    }


}
