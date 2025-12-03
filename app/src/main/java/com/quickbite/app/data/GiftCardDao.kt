package com.quickbite.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.quickbite.app.model.GiftCard

@Dao
interface GiftCardDao {
    @Insert
    suspend fun insert(giftCard: GiftCard)

    @Query("SELECT * FROM gift_cards WHERE code = :code")
    suspend fun findByCode(code: String): GiftCard?

    @Update
    suspend fun update(giftCard: GiftCard)
}
