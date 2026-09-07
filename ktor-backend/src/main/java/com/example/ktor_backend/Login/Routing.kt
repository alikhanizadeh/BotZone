package com.example.ktor_backend.Login



import com.example.ktor_backend.Product.productRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        // Auth Routes (Login/Register)
        authRoutes()

        // Product Routes
        productRoutes()

    }


}





