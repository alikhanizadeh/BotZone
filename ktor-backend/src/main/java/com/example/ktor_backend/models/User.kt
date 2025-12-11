package com.example.ktor_backend.models


import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val deviceId: String,
    val createdAt: String,
    val lastLogin: String? = null,
    val isActive: Boolean = true
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val deviceId: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val username: String? = null
)
