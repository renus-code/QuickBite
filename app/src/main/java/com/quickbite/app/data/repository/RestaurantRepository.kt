package com.quickbite.app.data.repository;

import com.quickbite.app.data.remote.ApiClient;
import com.quickbite.app.model.Restaurant

class RestaurantRepository {

    private val api = ApiClient.restaurantApiService

    suspend fun getRestaurants(): List<Restaurant> {
        return try {
            api.getRestaurants()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
