package com.example.botzone.Room.Conferences


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conferences")
data class ConferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: String,
    val location: String,
    val speaker: String,
    val imageUrl: Int
)