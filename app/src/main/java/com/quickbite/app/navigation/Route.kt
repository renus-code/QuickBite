package com.quickbite.app.navigation

sealed class Route(val route: String, val title: String) {
    object Restaurants : Route("restaurants", "Restaurants")
    object GiftCards : Route("giftCards", "Gift Cards")
    object Cart : Route("cart", "Cart")
    object Activity : Route("activity", "Activity")
    object Account : Route("account", "Account")
    object Settings : Route("settings", "Settings")
}