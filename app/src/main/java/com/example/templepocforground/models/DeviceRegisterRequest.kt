package com.example.templepocforground.models

data class DeviceRegisterRequest(
    val deviceId: String,
    val token: String,
    val platform : String,
    val userid : String,
    val tags: List<String>
)
