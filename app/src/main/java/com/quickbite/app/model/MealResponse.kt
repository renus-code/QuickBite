package com.quickbite.app.model

data class MealResponse(
    val meals: List<Meal>?
)

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)
