
package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quickbite.app.data.GiftCardRepository
import com.quickbite.app.data.UserRepository
import com.quickbite.app.util.SettingsManager

class UserViewModelFactory(
    private val repository: UserRepository,
    private val giftCardRepository: GiftCardRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository, giftCardRepository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
