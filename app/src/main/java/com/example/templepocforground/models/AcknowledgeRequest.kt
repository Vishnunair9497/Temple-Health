package com.example.templepocforground.models

data class AcknowledgeRequest(
    /*val NotificationId: String,
    val ProviderId: String,*/
    val AlertId: String,
    val UserId: String
)

data class AcknowledgeResponse(
    val success: Boolean,
    val message: String? = null
)