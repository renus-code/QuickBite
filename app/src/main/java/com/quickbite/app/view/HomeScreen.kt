package com.quickbite.app.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quickbite.app.components.BottomNavigationBar
import com.quickbite.app.navigation.BottomNavGraph
import com.quickbite.app.navigation.Route
import com.quickbite.app.ui.screens.*
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

    var selectedTab by remember { mutableStateOf(Route.Restaurants.route) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding -> 

        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                Route.Restaurants.route -> {
                    // Reuse existing logic for Restaurants
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
                        modifier = Modifier
                    )
                }

                Route.GiftCards.route -> {
                    // Payment tab NavHost
                    NavHost(
                        navController = giftCardsNavController,
                        startDestination = "payments_home"
                    ) {
                        composable("payments_home") {
                            PaymentsScreen(navController = giftCardsNavController, userVM = userVM)
                        }
                        composable("gift_card_landing") {
                            GiftCardLandingScreen(navController = giftCardsNavController)
                        }
                        composable("purchaseGiftCard") {
                            PurchaseGiftCardScreen(navController = giftCardsNavController, userVM = userVM)
                        }
                        composable("redeemGiftCard") {
                            RedeemGiftCardScreen(navController = giftCardsNavController, userVM = userVM)
                        }
                        composable("activity") {
                             ActivityScreen(menuVM = menuVM)
                        }
                    }
                }

                Route.Cart.route -> CartScreen(menuVM, cartNavController, isBottomNav = true)
                
                Route.Activity.route -> ActivityScreen(menuVM)
                
                Route.Account.route -> {
                    // FIX: Use a NavHost for Account tab to handle navigation to Settings
                    NavHost(
                        navController = accountNavController,
                        startDestination = "account_home"
                    ) {
                        composable("account_home") {
                            AccountScreen(
                                navController = accountNavController, 
                                userVM = userVM, 
                                onLogout = {
                                    parentNavController.navigate("signin") {
                                        popUpTo(parentNavController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(navController = accountNavController, userVM = userVM)
                        }
                    }
                }
            }
        }
    }
}
