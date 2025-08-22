package com.example.templepocforground.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun getNetworkType(context: Context): String {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return "No Connection"
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return "No Connection"

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile Data"
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
        else -> "Unknown"
    }
}
