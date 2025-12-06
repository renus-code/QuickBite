package com.quickbite.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.quickbite.app.ui.screens.RestaurantsScreen
import com.quickbite.app.ui.screens.MenuScreen
import com.quickbite.app.ui.screens.ActivityScreen
import com.quickbite.app.ui.screens.AccountScreen
import com.quickbite.app.ui.screens.SettingsScreen
import com.quickbite.app.ui.screens.GiftCardLandingScreen
import com.quickbite.app.ui.screens.CartScreen
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.RestaurantViewModel
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    userVM: UserViewModel,
    restaurantVM: RestaurantViewModel,
    menuVM: MenuViewModel,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Restaurants.route,
        modifier = Modifier.fillMaxSize()
    ) {

        // Restaurants Nested Graph
        navigation(
            startDestination = "restaurants/list",
            route = Route.Restaurants.route
        ) {
            composable("restaurants/list") {
                // Pass a lambda for handling restaurant click
                RestaurantsScreen(
                    navController = navController,
                    restaurantVM = restaurantVM,
                    onRestaurantClick = { restaurantName ->
                        navController.navigate("restaurants/menu/$restaurantName")
                    }
                )
            }

            composable("restaurants/menu/{restaurantName}") { backStackEntry ->
                val restaurantName = backStackEntry.arguments?.getString("restaurantName") ?: "Menu"
                MenuScreen(
                    menuVM = menuVM,
                    restaurantName = restaurantName,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Gift Cards
        composable(Route.GiftCards.route) {
            GiftCardLandingScreen(navController = navController)
        }

        // Cart
        composable(Route.Cart.route) {
            CartScreen(
                menuVM = menuVM,
                navController = navController,
                isBottomNav = true
            )
        }

        // Activity
        composable(Route.Activity.route) {
            ActivityScreen(menuVM = menuVM)
        }

        // Account
        composable(Route.Account.route) {
            AccountScreen(navController = navController, userVM = userVM, onLogout = onLogout)
        }

        // Settings
        composable(Route.Settings.route) {
            SettingsScreen(navController = navController, userVM = userVM)
        }
    }
}
