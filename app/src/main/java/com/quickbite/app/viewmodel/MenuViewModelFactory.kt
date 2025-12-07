
package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.util.SettingsManager

class MenuViewModelFactory(
    private val orderRepository: OrderRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(orderRepository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
