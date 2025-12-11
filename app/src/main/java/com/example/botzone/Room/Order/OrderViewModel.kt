package com.example.botzone.Room.Order

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.botzone.Products.CartItem
import com.example.botzone.Room.Store.ProductEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    val _latestOrder = MutableStateFlow<OrderEntity?>(null)
    val latestOrder: StateFlow<OrderEntity?> = _latestOrder

    fun generateTrackingCode(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return buildString {
            append("RBTX-")
            repeat(4) { append(chars.random()) }
            append("-")
            repeat(4) { append(chars.random()) }
        }
    }

    fun saveOrder(cartItems: List<CartItem>, total: Double, qrPath: String?): OrderEntity {
        val products = cartItems.map {
            ProductEntity(
                title = it.name,
                subtitle = "Qty: ${it.quantity}",
                price = it.price.toString(),
                image = it.imageUrl,
                category = "cart"
            )
        }

        val order = OrderEntity(
            trackingCode = generateTrackingCode(),
            date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            total = total,
            qrCodePath = qrPath,
            itemsJson = Gson().toJson(products)
        )

        viewModelScope.launch {
            repository.addOrder(order)
            _latestOrder.value = order
        }
        return order
    }


    fun debugTestOrder(context: Context) {
        viewModelScope.launch {
            val test = OrderEntity(
                trackingCode = "TEST-123",
                date = "2025-11-01",
                total = 5000.0,
                qrCodePath = null,
                itemsJson = "[]"
            )
            repository.addOrder(test)

            val result = repository.getOrder("TEST-123")
            Log.d("OrderDebug", "Order result: $result")
        }
    }



    // ✅ نسخه جدید: گرفتن سفارش از دیتابیس با Live State
    fun loadOrderByCode(code: String) {
        viewModelScope.launch {
            val result = repository.getOrder(code)
            _latestOrder.value = result
        }
    }
}


class OrderViewModelFactory(
    private val repository: OrderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}