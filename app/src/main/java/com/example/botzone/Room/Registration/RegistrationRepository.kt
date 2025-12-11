package com.example.botzone.Room.Registration

// Repository
class RegistrationRepository(private val dao: RegistrationDao) {
    suspend fun save(registration: RegistrationEntity): Long {
        return dao.insert(registration)  // Long برگردون
    }
}