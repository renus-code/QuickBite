package com.quickbite.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object ApiClient {

    private const val MEAL_DB_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    private const val RESTAURANT_BASE_URL = "https://raw.githubusercontent.com/renus-code/restaurants-dataset/refs/heads/master/"

    val menuApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MEAL_DB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val restaurantApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RESTAURANT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}
