package com.example.templepocforground.models

data class PubSubMessage(
    val Id: String,
    val Message: String,
    val RecipientId: String,
    val Timestamp: String,
    val Category: String,
    val Gender: String,
    val Injury: String,
    val PtNo: String,
    val Consideration: String,
    val Count: Int
)
