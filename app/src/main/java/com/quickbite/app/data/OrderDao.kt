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

    @Query("SELECT * FROM orders WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getOrdersForUser(email: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE userEmail = :email AND timestamp >= :since ORDER BY timestamp DESC")
    fun getOrdersSinceForUser(email: String, since: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE userEmail = :email AND timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getOrdersByDateRangeForUser(email: String, startDate: Long, endDate: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :id")
    fun getOrderById(id: Int): Flow<Order>

    @Query("SELECT * FROM orders WHERE userEmail = :email AND status = :status ORDER BY timestamp DESC")
    fun getOrdersByStatusForUser(email: String, status: String): Flow<List<Order>>
}
