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

data class AlertResponse(
    val alertId: String,
    val iteration: Int,
    val title: String,
    val body: String,
    val data: AlertData
)

data class AlertData(
    val Gender: String,
    val Category: String,
    val Injury: String,
    val PtNo: String,
    val Consideration: String
)
