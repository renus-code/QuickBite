package com.quickbite.app.navigation

sealed class Screen(val route: String, val title: String) {
    object Restaurants : Screen("restaurants", "Menu")
    object GiftCards : Screen("giftCards", "Gift Cards") // Replaced Services with Gift Cards
    object Cart : Screen("cart", "Cart")
    object Activity : Screen("activity", "Activity")
    object Account : Screen("account", "Account")
    object Settings : Screen("settings", "Settings")
}
