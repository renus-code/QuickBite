package com.quickbite.app.data.repository

import com.quickbite.app.data.remote.ApiClient
import com.quickbite.app.model.FoodItem
import kotlin.ranges.random
import kotlin.text.toIntOrNull

class MenuRepository {

    private val api = ApiClient.menuApiService

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


    suspend fun getMealsByFirstLetter(firstLetter: Char? = null): List<FoodItem> {
        val response = if (firstLetter != null) {
            api.getMealsByFirstLetter(firstLetter.toString())
        } else {
            api.getMeals()
        }
        val meals = response.meals ?: emptyList()

        return meals.map { meal ->
            FoodItem(
                id = meal.idMeal.toIntOrNull() ?: 0,
                name = meal.strMeal,
                price = ((5..20).random() + 0.99),
                imageUrl = meal.strMealThumb
            )
        }
    }

}
