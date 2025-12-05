package com.quickbite.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quickbite.app.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getRecentOrders(): Flow<List<Order>>
}
