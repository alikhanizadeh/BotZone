package com.example.botzone.Login.Models


data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String
)



data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val username: String?
)