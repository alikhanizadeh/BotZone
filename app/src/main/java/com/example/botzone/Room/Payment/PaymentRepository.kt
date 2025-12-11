package com.example.botzone.Room.Payment

// Repository
class PaymentRepository(private val dao: PaymentDao) {
    suspend fun save(payment: PaymentEntity) = dao.insert(payment)
}