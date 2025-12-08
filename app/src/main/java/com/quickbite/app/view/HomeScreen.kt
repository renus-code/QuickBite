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
import androidx.navigation.navArgument
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
                        }
                    )
                }

                Route.GiftCards.route -> {
                    NavHost(
                        navController = giftCardsNavController,
                        startDestination = "gift_card_landing"
                    ) {
                        composable("gift_card_landing") {
                            GiftCardLandingScreen(navController = giftCardsNavController)
                        }
                        composable("purchaseGiftCard") {
                            PurchaseGiftCardScreen(navController = giftCardsNavController, userVM = userVM)
                        }
                        composable("redeemGiftCard") {
                            RedeemGiftCardScreen(navController = giftCardsNavController, userVM = userVM)
                        }
                    }
                }

                Route.Cart.route -> {
                    NavHost(
                        navController = cartNavController,
                        startDestination = "cart_home"
                    ) {
                        composable("cart_home") {
                            CartScreen(menuVM = menuVM, navController = cartNavController, isBottomNav = true)
                        }
                        composable("checkout") {
                            CheckoutScreen(userVM = userVM, menuVM = menuVM, navController = cartNavController)
                        }
                    }
                }
                
                Route.Activity.route -> {
                    val activityNavController = rememberNavController()
                    NavHost(
                        navController = activityNavController,
                        startDestination = "activity_home"
                    ) {
                        composable("activity_home") {
                            ActivityScreen(menuVM = menuVM, navController = activityNavController)
                        }
                        composable(
                            "order_detail/{orderId}",
                            arguments = listOf(navArgument("orderId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
                            OrderDetailScreen(orderId = orderId, menuVM = menuVM, navController = activityNavController)
                        }
                    }
                }
                
                Route.Account.route -> {
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
                        composable("address_book") {
                            AddressBookScreen(navController = accountNavController, userVM = userVM)
                        }
                        composable("add_address") {
                            AddAddressScreen(navController = accountNavController, userVM = userVM)
                        }
                    }
                }
            }
        }
    }
}
