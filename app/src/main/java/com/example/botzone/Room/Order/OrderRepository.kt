package com.example.botzone.Room.Order



class OrderRepository(private val orderDao: OrderDao) {

    suspend fun addOrder(order: OrderEntity) = orderDao.insertOrder(order)

    suspend fun getOrder(trackingCode: String): OrderEntity? =
        orderDao.getOrderByTrackingCode(trackingCode)
}
