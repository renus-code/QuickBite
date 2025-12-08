package com.quickbite.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.quickbite.app.data.Converters

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    val email: String,
    val password: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val addresses: List<Address> = emptyList(), // Changed to a list of Addresses
    val paymentMethod: String? = null,
    val avatarId: String? = null,
    val balance: Double = 0.0,
    val isDarkMode: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
