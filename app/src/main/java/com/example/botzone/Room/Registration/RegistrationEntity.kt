package com.example.botzone.Room.Registration


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registrations")
data class RegistrationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val conferenceId: Int,           // کد کنفرانس
    val fullName: String,
    val email: String,
    val phone: String,
    val company: String?,            // اختیاری
    val timestamp: Long = System.currentTimeMillis()
)