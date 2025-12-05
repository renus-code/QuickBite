package com.quickbite.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gift_cards")
data class GiftCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val amount: Double,
    val senderName: String,
    val recipientName: String,
    val recipientEmail: String,
    val isRedeemed: Boolean = false,
    val redeemedByUserId: Int? = null // Nullable, linking to User ID if redeemed
)
