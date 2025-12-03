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
import com.quickbite.app.ui.screens.ServicesScreen
import com.quickbite.app.ui.screens.ActivityScreen
import com.quickbite.app.ui.screens.AccountScreen
import com.quickbite.app.ui.screens.SettingsScreen
import com.quickbite.app.ui.screens.ServiceDetailScreen
import com.quickbite.app.ui.screens.GiftCardLandingScreen
import com.quickbite.app.ui.screens.PurchaseGiftCardScreen
import com.quickbite.app.ui.screens.RedeemGiftCardScreen

import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    userVM: UserViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Services.route, // Changed for independent development
        modifier = modifier
    ) {
        // RESTAURANTS SCREEN
        composable(Screen.Restaurants.route) {
            RestaurantsScreen()
        }

        // SERVICES SCREEN
        composable(Screen.Services.route) {
            ServicesScreen(
                userVM = userVM,
                navController = navController
            )
        }

        // ACTIVITY SCREEN
        composable(Screen.Activity.route) {
            ActivityScreen()
        }

        // ACCOUNT SCREEN
        composable(Screen.Account.route) {
            AccountScreen(
                navController = navController,
                userVM = userVM
            )
        }

        // SETTINGS SCREEN
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                userVM = userVM
            )
        }

        // SERVICE DETAIL SCREEN
        composable(
            route = "serviceDetail/{serviceName}",
            arguments = listOf(navArgument("serviceName") { defaultValue = "Unknown" })
        ) { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName")
            if (serviceName == "Gift Cards") {
                GiftCardLandingScreen(navController)
            } else {
                ServiceDetailScreen(serviceName)
            }
        }

        // GIFT CARD ROUTES
        composable("giftCardLanding") { GiftCardLandingScreen(navController) }
        composable("purchaseGiftCard") { PurchaseGiftCardScreen(navController, userVM) }
        composable("redeemGiftCard") { RedeemGiftCardScreen(navController, userVM) }
    }
}
