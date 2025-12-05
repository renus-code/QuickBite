package com.quickbite.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quickbite.app.navigation.Route

@Composable
fun BottomNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        Route.Restaurants,
        Route.GiftCards,
        Route.Cart,
        Route.Activity,
        Route.Account
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { screen ->
            val isSelected = currentTab == screen.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen.route) {
                            Route.Restaurants.route -> Icons.Filled.Fastfood
                            Route.GiftCards.route -> Icons.Filled.CardGiftcard
                            Route.Cart.route -> Icons.Filled.ShoppingCart
                            Route.Activity.route -> Icons.Filled.Person
                            Route.Account.route -> Icons.Filled.AccountCircle
                            else -> Icons.Filled.Person
                        },
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onTabSelected(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Blue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Blue
                )
            )
        }
    }
}
