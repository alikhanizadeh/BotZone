package com.example.ktor_backend.Product

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int = 0,
    val title: String,
    val subtitle: String,
    val price: String,
    val imagePath: String,  // مسیر محلی فایل (مثل: /images/product_1.jpg)
    val category: String
)


@Serializable
data class ProductResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<Product>? = null
)

@Serializable
data class SingleProductResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Product? = null
)

@Serializable
data class UploadResponse(
    val success: Boolean,
    val message: String,
    val imagePath: String? = null  // مسیر عکس آپلود شده
)


@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)