package com.example.botzone.Login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botzone.Login.Models.LoginRequest
import com.example.botzone.Login.Models.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


//مدیریت فرآیند Login و ارتباط با UI

class LoginViewModel(
    private val repo: AuthRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val username = MutableStateFlow("")
    val password = MutableStateFlow("")


    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun loginOrRegister(deviceId: String) {
        viewModelScope.launch {

            _state.value = LoginState(loading = true)


            val req = LoginRequest(
                username = username.value,
                password = password.value,
                deviceId = deviceId
            )

            // Step1 → Login
            val loginRes = repo.login(req)

            if (loginRes.isSuccessful && loginRes.body()?.success == true) {
                val body = loginRes.body()!!
                prefs.saveLogin(body.token!!, body.username!!, deviceId)

                _state.value = LoginState(success = true)
                return@launch
            }

            // Step2 → Register
            val registerRes = repo.register(req)

            if (registerRes.isSuccessful && registerRes.body()?.success == true) {
                val body = registerRes.body()!!
                prefs.saveLogin(body.token!!, body.username!!, deviceId)

                _state.value = LoginState(success = true)
                return@launch
            }

            // Error
            _state.value = LoginState(
                error = registerRes.body()?.message ?: "خطای نامشخص"
            )
        }
    }
}
