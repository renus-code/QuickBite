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

    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    fun getOrdersSince(timestamp: Long): Flow<List<Order>> {
        return orderDao.getOrdersSince(timestamp)
    }

    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<Order>> {
        return orderDao.getOrdersByDateRange(startDate, endDate)
    }

    fun getOrderById(id: Int): Flow<Order> {
        return orderDao.getOrderById(id)
    }

    fun getOrdersByStatus(status: String): Flow<List<Order>> {
        return orderDao.getOrdersByStatus(status)
    }
}