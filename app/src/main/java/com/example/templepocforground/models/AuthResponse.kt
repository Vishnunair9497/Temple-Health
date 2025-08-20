package com.example.templepocforground.models

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)
data class AuthRequest(
    val email: String,
    val password: String
)