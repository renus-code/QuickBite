package com.quickbite.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.quickbite.app.ui.screens.*
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.RestaurantViewModel
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    userVM: UserViewModel,
    restaurantVM: RestaurantViewModel,
    menuVM: MenuViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Restaurants.route,
        modifier = modifier
    ) {

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

        navigation(
            startDestination = "gift_card_landing",
            route = Route.GiftCards.route
        ) {
            composable("gift_card_landing") {
                GiftCardLandingScreen(navController = navController)
            }
            composable("purchaseGiftCard") {
                PurchaseGiftCardScreen(navController = navController, userVM = userVM)
            }
            composable("redeemGiftCard") {
                RedeemGiftCardScreen(navController = navController, userVM = userVM)
            }
        }

        composable(Route.Cart.route) {
            CartScreen(menuVM = menuVM, navController = navController, isBottomNav = true)
        }

        composable(Route.Activity.route) {
            ActivityScreen(menuVM = menuVM, navController = navController)
        }

        composable(Route.Account.route) {
            AccountScreen(navController = navController, userVM = userVM, onLogout = onLogout)
        }

        composable(Route.Settings.route) {
            SettingsScreen(navController = navController, userVM = userVM)
        }
        
        composable(
            "order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailScreen(orderId = orderId, menuVM = menuVM, navController = navController)
        }
    }
}
