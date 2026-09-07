package com.example.botzone.Login

import com.example.botzone.Login.Models.LoginRequest


//واسط بین ViewModel و API

class AuthRepository {

    private val api = RetrofitInstance.authApi

    suspend fun login(request: LoginRequest) =
        api.login(request)

    suspend fun register(request: LoginRequest) =
        api.register(request)
}
