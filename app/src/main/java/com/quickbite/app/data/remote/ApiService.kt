package com.quickbite.app.data.remote

import com.quickbite.app.model.MealResponse
import retrofit2.http.GET

interface ApiService {

    @GET("search.php?s=")
    suspend fun getMeals(): MealResponse
}
