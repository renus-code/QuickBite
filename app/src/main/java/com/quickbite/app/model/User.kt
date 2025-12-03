package com.quickbite.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val email: String,
    val password: String,
    val displayName: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
