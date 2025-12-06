package com.quickbite.app.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.data.repository.MenuRepository
import com.quickbite.app.model.CartItem
import com.quickbite.app.model.FoodItem
import com.quickbite.app.model.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val menuRepo = MenuRepository()

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _showOrderStatusDialog = MutableStateFlow(false)
    val showOrderStatusDialog: StateFlow<Boolean> = _showOrderStatusDialog

    private val _orderStatusMessage = MutableStateFlow("")
    val orderStatusMessage: StateFlow<String> = _orderStatusMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(_foodItems, _searchQuery) { items, query ->
        if (query.isBlank()) items else items.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------ Load meals based on restaurant ------------------
    fun loadMealsForRestaurant(restaurantName: String) {
        val firstChar = restaurantName.firstOrNull()?.lowercaseChar() ?: 'a'

        viewModelScope.launch {
            try {
                val meals = menuRepo.getMealsByFirstLetter(firstChar)
                _foodItems.value = meals
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to load meals"
            }
        }
    }

    // ------------------ Cart functions ------------------
    fun addToCart(item: FoodItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.item.id == item.id }

        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(item, 1))
        }
        _cartItems.value = current
    }

    fun increaseQuantity(item: FoodItem) { /* ... same as before ... */ }
    fun decreaseQuantity(item: FoodItem) { /* ... same as before ... */ }
    fun clearCart() { _cartItems.value = emptyList() }

    // ------------------ Order functions ------------------
    fun placeOrder() { /* ... same as before ... */ }

    // ------------------ Search functions ------------------
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onSearchTriggered(query: String) { /* ... same as before ... */ }
    fun clearRecentSearches() { _recentSearches.value = emptyList() }

    // ------------------ Recent Orders ------------------
    val recentOrders: StateFlow<List<Order>> = orderRepository.getRecentOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
