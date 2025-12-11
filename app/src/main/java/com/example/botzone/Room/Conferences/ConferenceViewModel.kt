package com.example.botzone.Room.Conferences




import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class ConferenceViewModel(
    private val repository: ConferenceRepository
) : ViewModel() {

    val allConferences = repository.allConferences

    fun insertInitialData() {
        viewModelScope.launch {
            repository.insertInitialData()
        }
    }
}




class ConferenceViewModelFactory(
    private val repository: ConferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConferenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConferenceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}