package com.example.botzone.SplashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botzone.Login.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SplashViewModel constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun checkLoginStatus() {
        viewModelScope.launch {
            prefs.token.collect { token ->
                _isLoggedIn.value = !token.isNullOrEmpty()
            }
        }
    }
}
