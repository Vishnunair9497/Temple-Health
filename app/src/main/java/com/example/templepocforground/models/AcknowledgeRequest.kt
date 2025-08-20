package com.example.templepocforground.models

data class AcknowledgeRequest(
    val NotificationId: String,
    val ProviderId: String
)

data class AcknowledgeResponse(
    val success: Boolean,
    val message: String? = null
)