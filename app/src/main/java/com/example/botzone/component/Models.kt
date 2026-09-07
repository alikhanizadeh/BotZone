package com.example.botzone.component

data class TestProduct(
    val id: Int,
    val title: String,
    val subtitle: String,
    val price: String,
    val imagePath: Int,
    val category: String
)

data class Product(
    val title: String,
    val subtitle: String,
    val price: String,
    val imagePath: Int // حالا URL سرور هست نه Resource ID
)