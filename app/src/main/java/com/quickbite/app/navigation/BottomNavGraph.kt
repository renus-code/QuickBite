package com.quickbite.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// IMPORTANT: Import ONLY the correct RestaurantsScreen from /ui/view/
import com.quickbite.app.ui.view.RestaurantsScreen

// Import other screens individually to avoid conflicts
import com.quickbite.app.ui.screens.ActivityScreen
import com.quickbite.app.ui.screens.AccountScreen
import com.quickbite.app.ui.screens.SettingsScreen
import com.quickbite.app.ui.screens.ServiceDetailScreen
import com.quickbite.app.ui.screens.GiftCardLandingScreen
import com.quickbite.app.ui.screens.PurchaseGiftCardScreen
import com.quickbite.app.ui.screens.RedeemGiftCardScreen
import com.quickbite.app.ui.screens.CartScreen

import com.quickbite.app.viewmodel.UserViewModel
import com.quickbite.app.viewmodel.RestaurantViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    userVM: UserViewModel,
    restaurantVM: RestaurantViewModel,
    onLogout: () -> Unit = {}, 
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Restaurants.route, 
        modifier = modifier
    ) {
        // RESTAURANTS SCREEN (Menu)
        composable(Screen.Restaurants.route) {
            RestaurantsScreen(restaurantVM = restaurantVM)
        }

        // GIFT CARDS SCREEN (Replaced Services)
        composable(Screen.GiftCards.route) {
            GiftCardLandingScreen(navController = navController)
        }
        
        // CART SCREEN
        composable(Screen.Cart.route) {
            CartScreen(
                restaurantVM = restaurantVM, 
                navController = navController,
                isBottomNav = true 
            )
        }

        // ACTIVITY SCREEN
        composable(Screen.Activity.route) {
            ActivityScreen(restaurantVM = restaurantVM)
        }

        // ACCOUNT SCREEN
        composable(Screen.Account.route) {
            AccountScreen(
                navController = navController,
                userVM = userVM,
                onLogout = onLogout
            )
        }

        // SETTINGS SCREEN
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                userVM = userVM
            )
        }

        // SERVICE DETAIL SCREEN (Kept if needed for other flows, but Gift Cards now direct)
        composable(
            route = "serviceDetail/{serviceName}",
            arguments = listOf(navArgument("serviceName") { defaultValue = "Unknown" })
        ) { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName")
            // Navigate to landing if specifically called with "Gift Cards"
            if (serviceName == "Gift Cards") {
                 GiftCardLandingScreen(navController)
            } else {
                 ServiceDetailScreen(serviceName, navController)
            }
        }

        // GIFT CARD ROUTES
        composable("giftCardLanding") { GiftCardLandingScreen(navController) }
        composable("purchaseGiftCard") { PurchaseGiftCardScreen(navController, userVM) }
        composable("redeemGiftCard") { RedeemGiftCardScreen(navController, userVM) }
    }
}
