
package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quickbite.app.data.repository.RestaurantRepository
import com.quickbite.app.util.SettingsManager

class RestaurantViewModelFactory(
    private val repository: RestaurantRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
