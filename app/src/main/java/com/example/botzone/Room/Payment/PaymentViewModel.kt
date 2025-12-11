package com.example.botzone.Room.Payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ViewModel
class PaymentViewModel(private val repository: PaymentRepository) : ViewModel() {
    suspend fun savePayment(payment: PaymentEntity) {
        return repository.save(payment)
    }

}

class PaymentViewModelFactory(
    private val repository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}