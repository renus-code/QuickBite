package com.quickbite.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quickbite.app.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getOrdersSince(since: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :id")
    fun getOrderById(id: Int): Flow<Order>
}
