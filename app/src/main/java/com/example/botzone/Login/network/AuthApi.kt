package com.example.botzone.Login.network


import com.example.botzone.Login.Models.LoginRequest
import com.example.botzone.Login.Models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val username: String,
    val password: String,
    val deviceId: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val username: String?
)

interface AuthApi {

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: LoginRequest): Response<LoginResponse>
}
