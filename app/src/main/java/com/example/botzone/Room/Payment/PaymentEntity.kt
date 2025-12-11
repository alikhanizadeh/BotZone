package com.example.botzone.Room.Payment

import androidx.room.Entity
import androidx.room.PrimaryKey

// File: Room/Payment/PaymentEntity.kt
@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val conferenceId: Int,
    val registrationId: Int, // از مرحله قبل
    val receiptImagePath: String, // مسیر فایل در حافظه داخلی
    val trackingCode: String,
    val timestamp: Long = System.currentTimeMillis()
)