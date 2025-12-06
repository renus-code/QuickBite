package com.quickbite.app.ui.view

import androidx.compose.foundation.layout.Box
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
    ) { innerPadding -> // This padding is crucial

        // This Box will apply the padding to whichever screen is currently selected.
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                Route.Restaurants.route -> {
                    // Use Restaurants nested NavGraph
                    // Remove the padding from here since the parent Box handles it now.
                    BottomNavGraph(
                        navController = restaurantNavController,
                        userVM = userVM,
                        restaurantVM = restaurantVM,
                        menuVM = menuVM,
                        onLogout = {
                            parentNavController.navigate("signin") {
                                popUpTo(parentNavController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier // Padding is now handled by the parent Box
                    )
                }

                // All other screens are now correctly padded by the parent Box
                Route.GiftCards.route -> GiftCardLandingScreen(giftCardsNavController)
                Route.Cart.route -> CartScreen(menuVM, cartNavController, isBottomNav = true)
                Route.Activity.route -> ActivityScreen(menuVM)
                Route.Account.route -> AccountScreen(accountNavController, userVM) {
                    parentNavController.navigate("signin") {
                        popUpTo(parentNavController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }
}

