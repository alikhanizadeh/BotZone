package com.example.botzone.Room.Registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ViewModel
class RegistrationViewModel(
    private val repository: RegistrationRepository
) : ViewModel() {
    suspend fun saveRegistration(reg: RegistrationEntity): Long {
        return repository.save(reg)  // Long برگردون
    }
}


class RegistrationViewModelFactory(
    private val repository: RegistrationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}