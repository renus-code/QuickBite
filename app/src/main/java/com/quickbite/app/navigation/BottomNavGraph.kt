package com.quickbite.app.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.quickbite.app.ui.screens.*
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.RestaurantViewModel
import com.quickbite.app.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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

        // --- PAYMENTS & GIFT CARDS GRAPH ---
        navigation(
            startDestination = "payments_home",
            route = Route.GiftCards.route
        ) {
            // 1. Main Payments Hub
            composable("payments_home") {
                // Pass userVM here so PaymentsScreen can show balance
                PaymentsScreen(navController = navController, userVM = userVM)
            }

            // 2. Gift Card Landing
            composable("gift_card_landing") {
                GiftCardLandingScreen(navController = navController)
            }

            // 3. Purchase Screen
            composable("purchaseGiftCard") {
                PurchaseGiftCardScreen(navController = navController, userVM = userVM)
            }

            // 4. Redeem Screen
            composable("redeemGiftCard") {
                RedeemGiftCardScreen(navController = navController, userVM = userVM)
            }
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
