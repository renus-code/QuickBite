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

    // -------------------- Food Items --------------------
    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // -------------------- Search --------------------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(_foodItems, _searchQuery) { items, query ->
        if (query.isBlank()) items else items.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun onSearchTriggered(query: String) {
        if (query.isNotBlank()) {
            val history = _recentSearches.value.toMutableList()
            history.remove(query)
            history.add(0, query)
            if (history.size > 5) history.removeLast()
            _recentSearches.value = history
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    // -------------------- Cart --------------------
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(item: FoodItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.item.id == item.id }

        if (index >= 0) {
            // Increment quantity if already exists
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(item, 1))
        }
        _cartItems.value = current
    }

    fun increaseQuantity(item: FoodItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.item.id == item.id }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
            _cartItems.value = current
        }
    }

    fun decreaseQuantity(item: FoodItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.item.id == item.id }
        if (index >= 0) {
            val updatedQty = current[index].quantity - 1
            if (updatedQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = updatedQty)
            }
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // -------------------- Order --------------------
    private val _showOrderStatusDialog = MutableStateFlow(false)
    val showOrderStatusDialog: StateFlow<Boolean> = _showOrderStatusDialog

    private val _orderStatusMessage = MutableStateFlow("")
    val orderStatusMessage: StateFlow<String> = _orderStatusMessage

    fun placeOrder() {
        viewModelScope.launch {
            val currentCart = _cartItems.value
            if (currentCart.isNotEmpty()) {
                val totalPrice = currentCart.sumOf { it.item.price * it.quantity }
                val itemNames = currentCart.flatMap { List(it.quantity) { _ -> it.item.name } }

                val order = Order(
                    items = itemNames,
                    totalPrice = totalPrice,
                    timestamp = System.currentTimeMillis(),
                    status = "Completed"
                )

                orderRepository.saveOrder(order)

                _showOrderStatusDialog.value = true
                _orderStatusMessage.value = "Order Placed"
                delay(3000)
                _orderStatusMessage.value = "Food is being prepared"
                delay(3000)
                _orderStatusMessage.value = "Driver has picked up your order"
                delay(3000)
                _orderStatusMessage.value = "Order Delivered"
                delay(2000)
                _showOrderStatusDialog.value = false

                clearCart()
            }
        }
    }

    // -------------------- Recent Orders --------------------
    val recentOrders: StateFlow<List<Order>> = orderRepository.getRecentOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // -------------------- Load Food --------------------
    init {
        loadFoodItems()
    }

    fun loadFoodItems() {
        viewModelScope.launch {
            try {
                _foodItems.value = menuRepo.getFoodItems()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to load meals"
            }
        }
    }
}
