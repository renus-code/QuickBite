package com.quickbite.app.data

import com.quickbite.app.model.Order
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun saveOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    fun getRecentOrders(): Flow<List<Order>> {
        return orderDao.getRecentOrders()
    }
}
