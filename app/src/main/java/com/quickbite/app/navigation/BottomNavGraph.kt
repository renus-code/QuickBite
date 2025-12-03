package com.quickbite.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// IMPORTANT: Import ONLY the correct RestaurantsScreen from /ui/view/
import com.quickbite.app.ui.view.RestaurantsScreen

// Import other screens individually to avoid conflicts
import com.quickbite.app.ui.screens.ServicesScreen
import com.quickbite.app.ui.screens.ActivityScreen
import com.quickbite.app.ui.screens.AccountScreen
import com.quickbite.app.ui.screens.SettingsScreen

import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    userVM: UserViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Restaurants.route,
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
            ActivityScreen(
                userVM = userVM,
                navController = navController
            )
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
    }
}
