package com.example.botzone.data

import com.example.botzone.R
import com.example.botzone.component.TestProduct

val testProducts = listOf(

    TestProduct(
        id = 1,
        title = "Arduino Uno",
        subtitle = "برد توسعه آردوینو",
        price = "1,200,000 تومان",
        imagePath = R.drawable.img1,
        category = "بردهای توسعه"
    ),

    TestProduct(
        id = 2,
        title = "ESP32",
        subtitle = "ماژول WiFi و Bluetooth",
        price = "950,000 تومان",
        imagePath = R.drawable.img2,
        category = "بردهای توسعه"
    ),

    TestProduct(
        id = 3,
        title = "Servo Motor",
        subtitle = "موتور سروو MG996R",
        price = "850,000 تومان",
        imagePath = R.drawable.img3,
        category = "موتورها"
    ),

    TestProduct(
        id = 4,
        title = "DC Motor",
        subtitle = "موتور DC",
        price = "450,000 تومان",
        imagePath = R.drawable.img4,
        category = "موتورها"
    ),

    TestProduct(
        id = 5,
        title = "Ultrasonic Sensor",
        subtitle = "سنسور فاصله HC-SR04",
        price = "350,000 تومان",
        imagePath = R.drawable.img5,
        category = "سنسورها"
    ),

    TestProduct(
        id = 6,
        title = "Temperature Sensor",
        subtitle = "سنسور دما",
        price = "250,000 تومان",
        imagePath = R.drawable.img6,
        category = "سنسورها"
    )
)
