package com.example.botzone.Room.Payment

import androidx.room.Dao
import androidx.room.Insert

// File: Room/Payment/PaymentDao.kt
@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: PaymentEntity)
}