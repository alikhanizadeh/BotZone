package com.example.ktor_backend.Login.security


import org.mindrot.jbcrypt.BCrypt


// 1. هش کردن پسورد با bcrypt
// 2.مقایسه پسورد ورودی با هش ذخیره‌شده


object PasswordHasher {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }
}
