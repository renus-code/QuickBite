package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FoodItem(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String
)

class RestaurantViewModel : ViewModel() {

    private val repo = RestaurantRepository()

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadFoodItems()
    }

    fun loadFoodItems() {
        viewModelScope.launch {
            try {
                _foodItems.value = repo.getFoodItems()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed"
            }
        }
    }
}
