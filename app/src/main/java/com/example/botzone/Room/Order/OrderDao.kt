package com.example.botzone.Room.Order

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE trackingCode = :trackingCode LIMIT 1")
    suspend fun getOrderByTrackingCode(trackingCode: String): OrderEntity?
}
