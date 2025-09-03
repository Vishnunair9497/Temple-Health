package com.example.templepocforground.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.example.templepocforground.helper.NotificationHelper
import constants.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(private val context: Context) {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(checkInitialConnectionSafely())
    val isConnected = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            try {
                _isConnected.value = true
                NotificationHelper.showPushNotification(
                    context,
                    Constants.TEMPLE_HEALTH,
                    Constants.MESSAGE_ONLINE
                )
            } catch (e: Exception) {
                Log.e("NetworkMonitor", "Error handling network available: ${e.message}", e)
            }
        }

        override fun onLost(network: Network) {
            try {
                _isConnected.value = false
                NotificationHelper.showPushNotification(
                    context,
                    Constants.TEMPLE_HEALTH,
                    Constants.MESSAGE_OFFLINE
                )
            } catch (e: Exception) {
                Log.e("NetworkMonitor", "Error handling network lost: ${e.message}", e)
            }
        }
    }

    init {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Failed to register network callback: ${e.message}", e)
        }
    }
    
    private fun checkInitialConnectionSafely(): Boolean {
        return try {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error checking initial connection: ${e.message}", e)
            false
        }
    }


    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: IllegalArgumentException) {
            Log.w("NetworkMonitor", "Network callback already unregistered: ${e.message}")
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error unregistering network callback: ${e.message}", e)
        }
    }
}
