package com.example.botzone.Room.Registration



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Insert
    suspend fun insert(registration: RegistrationEntity) : Long

    @Query("SELECT * FROM registrations WHERE conferenceId = :conferenceId")
    fun getByConferenceId(conferenceId: Int): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE id = :id")
    suspend fun getById(id: Int): RegistrationEntity?
}