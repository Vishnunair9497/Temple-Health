package com.example.templepocforground.models

data class PubSubMessage(
    val Id: String,
    val Message: String,
    val RecipientId: String,
    val Timestamp: String,
    val Count: Int
)
