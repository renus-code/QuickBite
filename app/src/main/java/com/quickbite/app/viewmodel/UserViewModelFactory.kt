package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quickbite.app.data.GiftCardRepository
import com.quickbite.app.data.UserRepository

class UserViewModelFactory(
    private val repository: UserRepository,
    private val giftCardRepository: GiftCardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository, giftCardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
