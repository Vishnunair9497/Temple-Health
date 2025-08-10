package com.example.templepocforground.services

import androidx.compose.runtime.mutableStateListOf

object PubSubMessageStore {
    val messages = mutableStateListOf<String>()

    fun addMessage(msg: String) {
        messages.add(0, msg)
    }
}