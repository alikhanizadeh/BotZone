package com.example.ktor_backend.Login


import com.example.ktor_backend.Login.security.JwtConfig
import com.example.ktor_backend.Login.security.PasswordHasher
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


//دریافت درخواست HTTP از کلاینت
//پردازش Login/Register
//برگرداندن Response

fun Route.authRoutes() {
    val userRepo = UserRepository()

    route("/auth") {
        // Register
        post("/register") {
            val request = call.receive<RegisterRequest>()

            // Check if device already registered
            if (userRepo.isDeviceRegistered(request.deviceId)) {
                call.respond(HttpStatusCode.Forbidden, AuthResponse(
                    success = false,
                    message = "این دستگاه قبلاً ثبت شده است"
                )
                )
                return@post
            }

            // Check if username exists
            if (userRepo.findByUsername(request.username) != null) {
                call.respond(HttpStatusCode.Conflict, AuthResponse(
                    success = false,
                    message = "این نام کاربری قبلاً استفاده شده"
                )
                )
                return@post
            }

            // Hash password
            val passwordHash = PasswordHasher.hashPassword(request.password)

            // Create user
            val user = userRepo.createUser(
                request.username,
                passwordHash,
                request.deviceId
            )

            if (user != null) {
                val token = JwtConfig.generateToken(user.username, user.deviceId)
                call.respond(HttpStatusCode.Created, AuthResponse(
                    success = true,
                    message = "ثبت‌نام موفقیت‌آمیز بود",
                    token = token,
                    username = user.username
                )
                )
            } else {
                call.respond(HttpStatusCode.InternalServerError, AuthResponse(
                    success = false,
                    message = "خطا در ثبت‌نام"
                )
                )
            }
        }

        // Login
        post("/login") {
            val request = call.receive<LoginRequest>()

            val user = userRepo.findByUsername(request.username)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, AuthResponse(
                    success = false,
                    message = "نام کاربری یا رمز عبور اشتباه است"
                )
                )
                return@post
            }

            // Check device binding
            if (user.deviceId != request.deviceId) {
                call.respond(HttpStatusCode.Forbidden, AuthResponse(
                    success = false,
                    message = "این حساب روی دستگاه دیگری ثبت شده است"
                )
                )
                return@post
            }

            // Verify password
            if (!PasswordHasher.verifyPassword(request.password, user.passwordHash)) {
                call.respond(HttpStatusCode.Unauthorized, AuthResponse(
                    success = false,
                    message = "نام کاربری یا رمز عبور اشتباه است"
                )
                )
                return@post
            }

            // Update last login
            userRepo.updateLastLogin(user.username)

            val token = JwtConfig.generateToken(user.username, user.deviceId)
            call.respond(HttpStatusCode.OK, AuthResponse(
                success = true,
                message = "ورود موفقیت‌آمیز",
                token = token,
                username = user.username
            )
            )
        }
    }
}
