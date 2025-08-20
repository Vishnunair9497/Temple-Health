package com.example.templepocforground.services

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.templepocforground.models.PubSubMessage
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PubSubMessageStore {
    val messages = mutableStateListOf<PubSubMessage>()
    val connection = mutableStateListOf<String>()

    private val _reestablishSocket = MutableStateFlow(false)
    val reestablishSocket = _reestablishSocket.asStateFlow()


    fun addMessage(json: String) {
        try {
            Log.d("addMessage", "addMessage:>> ${json} ")
            val msg = Gson().fromJson(json, PubSubMessage::class.java)
            messages.add(0, msg)

            Log.d("TAG", "addMessage: ${messages.last()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun connectionStatus(msg: String) {
        connection.add(0, msg)

    }

    fun triggerReestablishSocket() {
        _reestablishSocket.value = true
    }

    fun resetReestablishSocket() {
        _reestablishSocket.value = false
    }
}