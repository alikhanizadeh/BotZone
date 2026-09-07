package com.example.ktor_backend.Login.security


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

// 1.ساخت JWT Token بعد از Login موفق
// 2.اعتبارسنجی Token‌های دریافتی


object JwtConfig {
    private const val SECRET = "your-secret-key-change-this-in-production"
    private const val ISSUER = "botzone-backend"
    private const val VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L // 7 days

    fun generateToken(username: String, deviceId: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(username)
            .withClaim("device_id", deviceId)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(Algorithm.HMAC256(SECRET))
    }

    fun verifyToken(token: String): String? {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .build()
            val jwt = verifier.verify(token)
            jwt.subject
        } catch (e: Exception) {
            null
        }
    }
}
