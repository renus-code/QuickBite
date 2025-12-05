package com.quickbite.app.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.quickbite.app.components.BottomNavigationBar
import com.quickbite.app.navigation.BottomNavGraph
import com.quickbite.app.navigation.Route
import com.quickbite.app.ui.screens.AccountScreen
import com.quickbite.app.ui.screens.ActivityScreen
import com.quickbite.app.ui.screens.CartScreen
import com.quickbite.app.ui.screens.GiftCardLandingScreen
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.RestaurantViewModel
import com.quickbite.app.viewmodel.UserViewModel
@Composable
fun HomeScreen(
    parentNavController: NavHostController,
    userVM: UserViewModel,
    restaurantVM: RestaurantViewModel,
    menuVM: MenuViewModel
) {
    val restaurantNavController = rememberNavController()
    val giftCardsNavController = rememberNavController()
    val cartNavController = rememberNavController()
    val activityNavController = rememberNavController()
    val accountNavController = rememberNavController()

    val tabNavControllers = mapOf(
        Route.Restaurants.route to restaurantNavController,
        Route.GiftCards.route to giftCardsNavController,
        Route.Cart.route to cartNavController,
        Route.Activity.route to activityNavController,
        Route.Account.route to accountNavController
    )

    var selectedTab by remember { mutableStateOf(Route.Restaurants.route) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->

        tabNavControllers.forEach { (route, navController) ->
            if (route == selectedTab) {
                when (route) {
                    Route.Restaurants.route -> {
                        // Use Restaurants nested NavGraph
                        BottomNavGraph(
                            navController = navController,
                            userVM = userVM,
                            restaurantVM = restaurantVM,
                            menuVM = menuVM,
                            onLogout = {
                                parentNavController.navigate("signin") {
                                    popUpTo(parentNavController.graph.startDestinationId) { inclusive = true }
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    Route.GiftCards.route -> GiftCardLandingScreen(navController)
                    Route.Cart.route -> CartScreen(menuVM, navController, isBottomNav = true)
                    Route.Activity.route -> ActivityScreen(menuVM)
                    Route.Account.route -> AccountScreen(navController, userVM) {
                        parentNavController.navigate("signin") {
                            popUpTo(parentNavController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

