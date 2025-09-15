package com.example.templepocforground.models

data class DeviceRegisterResponse(
    val success: Boolean,
    val message: String,
    val registrationId: String,
    val action: String
)

