package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.model.Restaurant
import com.quickbite.app.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantViewModel(
    private val repository: RestaurantRepository = RestaurantRepository()
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // --- Search-related state ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredRestaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val filteredRestaurants: StateFlow<List<Restaurant>> = _filteredRestaurants

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    init {
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        viewModelScope.launch {
            try {
                val data = repository.getRestaurants()
                _restaurants.value = data
                _filteredRestaurants.value = data // initially show all
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    // --- Search functions ---
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _filteredRestaurants.value = _restaurants.value
        } else {
            _filteredRestaurants.value = _restaurants.value.filter {
                it.restaurantName?.contains(query, ignoreCase = true) == true ||
                        it.category?.contains(query, ignoreCase = true) == true
            }
        }
    }

    fun onSearchTriggered(query: String) {
        if (query.isNotEmpty()) {
            _recentSearches.update { listOf(query) + it.filterNot { existing -> existing == query } }
        }
        onSearchQueryChange(query)
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }
}
