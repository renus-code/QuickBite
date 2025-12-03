package com.quickbite.app.data.repository

import com.quickbite.app.data.remote.ApiClient
import com.quickbite.app.viewmodel.FoodItem

class RestaurantRepository {

    private val api = ApiClient.apiService

    suspend fun getFoodItems(): List<FoodItem> {
        val response = api.getMeals()
        val meals = response.meals ?: emptyList()

        return meals.map { meal ->
            FoodItem(
                id = meal.idMeal.toIntOrNull() ?: 0,
                name = meal.strMeal,
                price = ((5..20).random() + 0.99),   // random price
                imageUrl = meal.strMealThumb
            )
        }
    }

}
