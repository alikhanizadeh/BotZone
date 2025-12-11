package com.example.botzone.Room.Conferences


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConferenceDao {
    @Query("SELECT * FROM conferences ORDER BY date ASC")
    fun getAllConferences(): Flow<List<ConferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conferences: List<ConferenceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conference: ConferenceEntity)
}