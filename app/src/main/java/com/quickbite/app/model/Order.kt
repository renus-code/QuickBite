package com.quickbite.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.quickbite.app.data.Converters

@Entity(tableName = "orders")
@TypeConverters(Converters::class)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,
    val items: List<String>, // Storing item names as a list
    val totalPrice: Double,
    val timestamp: Long,
    var status: String
)
