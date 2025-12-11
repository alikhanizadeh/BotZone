package com.example.botzone.Fake

import androidx.lifecycle.viewModelScope
import com.example.botzone.Room.Order.OrderDao
import com.example.botzone.Room.Order.OrderEntity
import com.example.botzone.Room.Order.OrderRepository
import com.example.botzone.Room.Order.OrderViewModel
import kotlinx.coroutines.launch


class FakeOrderDao : OrderDao {
    private val orders = mutableListOf<OrderEntity>()

    override suspend fun insertOrder(order: OrderEntity) {
        orders.add(order)
    }

    override suspend fun getOrderByTrackingCode(trackingCode: String): OrderEntity? {
        return orders.find { it.trackingCode == trackingCode }
    }
}


class FakeOrderViewModel : OrderViewModel(
    repository = OrderRepository(FakeOrderDao())
) {
    init {
        // پر کردن داده اولیه برای نمایش در Preview
        viewModelScope.launch {
            _latestOrder.value = OrderEntity(
                id = 1,
                trackingCode = "RBTX-DEMO-1234",
                date = "2025-11-01 18:30",
                total = 350000.0,
                qrCodePath = null,
                itemsJson = """
                    [
                        {"id":1,"name":"ربات تعقیب خط","price":150000.0,"quantity":1},
                        {"id":2,"name":"سنسور فاصله‌سنج","price":200000.0,"quantity":1}
                    ]
                """.trimIndent()
            )
        }
    }
}







