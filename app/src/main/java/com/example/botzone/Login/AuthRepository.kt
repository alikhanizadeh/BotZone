package com.example.botzone.Login

import com.example.botzone.Login.Models.LoginRequest


class AuthRepository {

    private val api = RetrofitInstance.api

    suspend fun login(request: LoginRequest) =
        api.login(request)

    suspend fun register(request: LoginRequest) =
        api.register(request)
}
