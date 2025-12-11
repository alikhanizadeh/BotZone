package com.example.ktor_backend.database



import com.example.ktor_backend.models.User
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

class UserRepository {
    private val connection: Connection

    init {
        connection = DriverManager.getConnection("jdbc:sqlite:botzone.db")
        createTable()
    }

    private fun createTable() {
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                device_id TEXT UNIQUE NOT NULL,
                created_at TEXT NOT NULL,
                last_login TEXT,
                is_active INTEGER DEFAULT 1
            )
        """)
    }

    fun createUser(username: String, passwordHash: String, deviceId: String): User? {
        return try {
            val stmt = connection.prepareStatement("""
                INSERT INTO users (username, password_hash, device_id, created_at)
                VALUES (?, ?, ?, ?)
            """)
            val now = LocalDateTime.now().toString()
            stmt.setString(1, username)
            stmt.setString(2, passwordHash)
            stmt.setString(3, deviceId)
            stmt.setString(4, now)
            stmt.executeUpdate()

            findByUsername(username)
        } catch (e: Exception) {
            null
        }
    }

    fun findByUsername(username: String): User? {
        val stmt = connection.prepareStatement("""
            SELECT * FROM users WHERE username = ?
        """)
        stmt.setString(1, username)
        val result = stmt.executeQuery()

        return if (result.next()) {
            User(
                id = result.getInt("id"),
                username = result.getString("username"),
                passwordHash = result.getString("password_hash"),
                deviceId = result.getString("device_id"),
                createdAt = result.getString("created_at"),
                lastLogin = result.getString("last_login"),
                isActive = result.getInt("is_active") == 1
            )
        } else null
    }

    fun updateLastLogin(username: String) {
        val stmt = connection.prepareStatement("""
            UPDATE users SET last_login = ? WHERE username = ?
        """)
        stmt.setString(1, LocalDateTime.now().toString())
        stmt.setString(2, username)
        stmt.executeUpdate()
    }

    fun isDeviceRegistered(deviceId: String): Boolean {
        val stmt = connection.prepareStatement("""
            SELECT COUNT(*) FROM users WHERE device_id = ?
        """)
        stmt.setString(1, deviceId)
        val result = stmt.executeQuery()
        return result.next() && result.getInt(1) > 0
    }
}
