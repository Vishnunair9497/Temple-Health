package com.example.templepocforground.utils

import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import com.example.templepocforground.helper.NotificationHelper
import constants.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(checkInitialConnection())
    val isConnected = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.value = true
            NotificationHelper.showPushNotification(
                context,
                Constants.TITLE_CONNECTION,
                Constants.MESSAGE_ONLINE
            )
        }

        override fun onLost(network: Network) {
            _isConnected.value = false
            NotificationHelper.showPushNotification(
                context,
                Constants.TITLE_CONNECTION,
                Constants.MESSAGE_OFFLINE
            )
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun checkInitialConnection(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}