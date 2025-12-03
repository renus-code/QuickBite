package com.quickbite.app.data

import com.quickbite.app.model.GiftCard

class GiftCardRepository(private val giftCardDao: GiftCardDao) {

    suspend fun createGiftCard(giftCard: GiftCard) {
        giftCardDao.insert(giftCard)
    }

    suspend fun getGiftCardByCode(code: String): GiftCard? {
        return giftCardDao.findByCode(code)
    }

    suspend fun updateGiftCard(giftCard: GiftCard) {
        giftCardDao.update(giftCard)
    }
}
