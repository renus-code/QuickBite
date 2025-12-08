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

    fun getOrderById(id: Int): Flow<Order> {
        return orderDao.getOrderById(id)
    }
}
