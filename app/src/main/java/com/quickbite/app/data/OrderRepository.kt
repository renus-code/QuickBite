package com.quickbite.app.data

import com.quickbite.app.model.Order
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun saveOrder(order: Order): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    // Deprecated: getAllOrders now requires user email to filter correctly in multi-user environment
    fun getAllOrders(): Flow<List<Order>> {
        // Fallback to empty list or throw exception if this shouldn't be called without user context
        // Ideally, callers should migrate to getOrdersForUser
        // For now, returning empty flow to avoid breakage but indicating misuse
         return kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    fun getOrdersForUser(email: String): Flow<List<Order>> {
        return orderDao.getOrdersForUser(email)
    }

    fun getOrdersSinceForUser(email: String, timestamp: Long): Flow<List<Order>> {
        return orderDao.getOrdersSinceForUser(email, timestamp)
    }

    fun getOrdersByDateRangeForUser(email: String, startDate: Long, endDate: Long): Flow<List<Order>> {
        return orderDao.getOrdersByDateRangeForUser(email, startDate, endDate)
    }

    fun getOrderById(id: Int): Flow<Order> {
        return orderDao.getOrderById(id)
    }

    fun getOrdersByStatusForUser(email: String, status: String): Flow<List<Order>> {
        return orderDao.getOrdersByStatusForUser(email, status)
    }
}
