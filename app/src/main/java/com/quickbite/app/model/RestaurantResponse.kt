package com.quickbite.app.model


data class RestaurantResponse(
    val results: List<Recipe>
)

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val pricePerServing: Double
)
