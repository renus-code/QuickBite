package com.quickbite.app.navigation

sealed class Screen(val route: String, val title: String) {
    object Restaurants : Screen("restaurants", "Restaurants")
    object Services : Screen("services", "Services")
    object Activity : Screen("activity", "Activity")
    object Account : Screen("account", "Account")
    object Settings : Screen("settings", "Settings")
}
