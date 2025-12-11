package com.example.botzone.Room.Order

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders"
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackingCode: String,
    val date: String,           // پیشنهاد: تاریخ به فرمت ISO ذخیره شود
    val total: Double,
    val qrCodePath: String?,    // مسیر فایل تصویر QR در storage (nullable)
    val itemsJson: String       // آیتم‌ها به صورت JSON ذخیره می‌شوند
)