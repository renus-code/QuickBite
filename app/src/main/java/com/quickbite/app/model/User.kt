package com.quickbite.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val email: String,
    val password: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val paymentMethod: String? = null,
    val avatarId: String? = null,
    val balance: Double = 0.0,
    val isDarkMode: Boolean = false, // Added Dark Mode preference
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
