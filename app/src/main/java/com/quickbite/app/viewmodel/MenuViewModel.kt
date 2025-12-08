
package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.data.repository.MenuRepository
import com.quickbite.app.model.CartItem
import com.quickbite.app.model.FoodItem
import com.quickbite.app.model.Order
import com.quickbite.app.util.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(
    private val orderRepository: OrderRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val menuRepo = MenuRepository()

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cartItems = MutableStateFlow(settingsManager.getCart())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _showOrderStatusDialog = MutableStateFlow(false)
    val showOrderStatusDialog: StateFlow<Boolean> = _showOrderStatusDialog

    private val _orderStatusMessage = MutableStateFlow("")
    val orderStatusMessage: StateFlow<String> = _orderStatusMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recentSearches = MutableStateFlow(settingsManager.getRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(_foodItems, _searchQuery) { items, query ->
        if (query.isBlank()) items else items.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _cartItems.collect { items -> settingsManager.saveCart(items) }
        }
        viewModelScope.launch {
            _recentSearches.collect { searches -> settingsManager.saveRecentSearches(searches) }
        }
    }

    fun loadMealsForRestaurant(restaurantName: String) {
        val firstChar = restaurantName.firstOrNull()?.lowercaseChar() ?: 'a'
        viewModelScope.launch {
            try {
                _foodItems.value = menuRepo.getMealsByFirstLetter(firstChar)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load meals"
            }
        }
    }

    fun addToCart(item: FoodItem) {
        _cartItems.update { currentCart ->
            val existingItem = currentCart.find { it.item.id == item.id }
            if (existingItem != null) {
                currentCart.map { if (it.item.id == item.id) it.copy(quantity = it.quantity + 1) else it }
            } else {
                currentCart + CartItem(item, 1)
            }
        }
    }

    fun increaseQuantity(item: FoodItem) {
        _cartItems.update { currentCart ->
            currentCart.map {
                if (it.item.id == item.id) it.copy(quantity = it.quantity + 1) else it
            }
        }
    }

    fun decreaseQuantity(item: FoodItem) {
        _cartItems.update { currentCart ->
            val existingItem = currentCart.find { it.item.id == item.id }
            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    currentCart.map { 
                        if (it.item.id == item.id) it.copy(quantity = it.quantity - 1) else it 
                    }
                } else {
                    currentCart.filterNot { it.item.id == item.id }
                }
            } else {
                currentCart
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentCart = _cartItems.value
            if (currentCart.isNotEmpty()) {
                val order = Order(
                    items = currentCart.map { "${it.item.name} (x${it.quantity})" },
                    totalPrice = currentCart.sumOf { it.item.price * it.quantity },
                    timestamp = System.currentTimeMillis(),
                    status = "Pending"
                )
                orderRepository.saveOrder(order)
                _orderStatusMessage.value = "Order placed successfully!"
                _showOrderStatusDialog.value = true
                clearCart()
            } else {
                _orderStatusMessage.value = "Cannot place an empty order."
                _showOrderStatusDialog.value = true
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTriggered(query: String) {
        if (query.isNotBlank()) {
            val updatedSearches = (_recentSearches.value + query).distinct().take(5)
            _recentSearches.value = updatedSearches
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    val recentOrders: StateFlow<List<Order>> = orderRepository.getRecentOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
