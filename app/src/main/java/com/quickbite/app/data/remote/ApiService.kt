package com.quickbite.app.data.remote

import com.quickbite.app.model.MealResponse
import com.quickbite.app.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Get all meals
    @GET("search.php?s=")
    suspend fun getMeals(): MealResponse

    // Get meals by first letter
    @GET("search.php")
    suspend fun getMealsByFirstLetter(
        @Query("f") letter: String // 'f' is the parameter for first letter in TheMealDB
    ): MealResponse

    // Get restaurants
    @GET("restaurants_unique.json")
    suspend fun getRestaurants(): List<Restaurant>
}
