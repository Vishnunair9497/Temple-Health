package com.example.templepocforground.models

data class NegotiateModel(
    val expriresAt: String,
    val message: String,
    val tokenLifetimeMinutes: Int,
    val url: String
)