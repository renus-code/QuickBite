package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.data.repository.MenuRepository
import com.quickbite.app.model.Address
import com.quickbite.app.model.CartItem
import com.quickbite.app.model.FoodItem
import com.quickbite.app.model.Order
import com.quickbite.app.util.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class OrderFilter { ALL, WEEK, MONTH }

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

    private val _orderStatusMessage = MutableStateFlow<String?>(null)
    val orderStatusMessage: StateFlow<String?> = _orderStatusMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recentSearches = MutableStateFlow(settingsManager.getRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val _orderFilter = MutableStateFlow(OrderFilter.ALL)
    val orderFilter: StateFlow<OrderFilter> = _orderFilter

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val orders: StateFlow<List<Order>> = _orderFilter.flatMapLatest { filter ->
        when (filter) {
            OrderFilter.ALL -> orderRepository.getAllOrders()
            OrderFilter.WEEK -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -7)
                orderRepository.getOrdersSince(cal.timeInMillis)
            }
            OrderFilter.MONTH -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, -1)
                orderRepository.getOrdersSince(cal.timeInMillis)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList<Order>())

    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(foodItems, searchQuery) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<FoodItem>())

    fun setOrderFilter(filter: OrderFilter) {
        _orderFilter.value = filter
    }

    fun getOrderById(id: Int): Flow<Order> {
        return orderRepository.getOrderById(id)
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

    fun placeOrder(address: Address, paymentMethod: String) {
        viewModelScope.launch {
            val currentCart = _cartItems.value
            if (currentCart.isNotEmpty()) {
                val newOrder = Order(
                    items = currentCart.map { "${it.item.name} (x${it.quantity})" },
                    totalPrice = currentCart.sumOf { it.item.price * it.quantity },
                    timestamp = System.currentTimeMillis(),
                    status = "Pending",
                    shippingAddress = address.toDisplayString(),
                    paymentMethod = paymentMethod
                )
                val orderId = orderRepository.saveOrder(newOrder)
                clearCart()
                
                // Start simulating the order status updates
                simulateOrderStatus(orderId, newOrder)
            }
        }
    }
    
    private fun simulateOrderStatus(orderId: Long, order: Order) {
        viewModelScope.launch {
            _orderStatusMessage.value = "Order placed successfully!"
            delay(5000) 
            orderRepository.updateOrder(order.copy(orderId = orderId.toInt(), status = "Processing"))
            _orderStatusMessage.value = "Your order is being processed."
            delay(10000) 
            orderRepository.updateOrder(order.copy(orderId = orderId.toInt(), status = "Out for Delivery"))
            _orderStatusMessage.value = "Your order is out for delivery!"
            delay(15000) 
            orderRepository.updateOrder(order.copy(orderId = orderId.toInt(), status = "Delivered"))
            _orderStatusMessage.value = "Your order has been delivered!"
            delay(5000)
            _orderStatusMessage.value = null // Clear message
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
}
