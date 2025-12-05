package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.data.repository.MenuRepository
import com.quickbite.app.data.repository.RestaurantRepository
import com.quickbite.app.model.FoodItem
import com.quickbite.app.model.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class MenuViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val repo = MenuRepository()

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    // Filtered Items based on search query
    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(_foodItems, _searchQuery) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Cart State
    private val _cartItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val cartItems: StateFlow<List<FoodItem>> = _cartItems

    // Order Status State
    private val _showOrderStatusDialog = MutableStateFlow(false)
    val showOrderStatusDialog: StateFlow<Boolean> = _showOrderStatusDialog

    private val _orderStatusMessage = MutableStateFlow("")
    val orderStatusMessage: StateFlow<String> = _orderStatusMessage

    // Recent Orders for Activity Screen
    val recentOrders: StateFlow<List<Order>> = orderRepository.getRecentOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTriggered(query: String) {
        if (query.isNotBlank()) {
            val currentHistory = _recentSearches.value.toMutableList()
            // Remove if exists to move to top
            currentHistory.remove(query)
            // Add to start
            currentHistory.add(0, query)
            // Keep max 5
            if (currentHistory.size > 5) {
                currentHistory.removeAt(currentHistory.lastIndex)
            }
            _recentSearches.value = currentHistory
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun addToCart(foodItem: FoodItem) {
        val currentCart = _cartItems.value.toMutableList()
        currentCart.add(foodItem)
        _cartItems.value = currentCart
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentCart = _cartItems.value
            if (currentCart.isNotEmpty()) {
                // Calculate total price
                val totalPrice = currentCart.sumOf { it.price }
                // Get list of item names
                val itemNames = currentCart.map { it.name }

                // Create Order object
                val order = Order(
                    items = itemNames,
                    totalPrice = totalPrice,
                    timestamp = System.currentTimeMillis(),
                    status = "Completed" // Initially mark as completed for history, or track status updates
                )

                // Save to database
                orderRepository.saveOrder(order)

                // Start simulation
                _showOrderStatusDialog.value = true
                _orderStatusMessage.value = "Order Placed"
                delay(5000)
                _orderStatusMessage.value = "Food is being prepared"
                delay(5000)
                _orderStatusMessage.value = "Driver has picked up your order"
                delay(5000)
                _orderStatusMessage.value = "Order Delivered"
                delay(2000) // Keep delivered message for 2s
                _showOrderStatusDialog.value = false
                clearCart()
            }
        }
    }
}
