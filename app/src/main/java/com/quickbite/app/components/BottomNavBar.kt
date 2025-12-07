package com.quickbite.app.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickbite.app.navigation.Route

@Composable
fun BottomNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        Route.Restaurants,
        Route.Cart,      // MOVED: Cart is now 2nd
        Route.GiftCards, // MOVED: Payments is now 3rd
        Route.Activity,
        Route.Account
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.height(86.dp),
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentTab == screen.route

            NavigationBarItem(
                modifier = Modifier.padding(top = 8.dp),
                icon = {
                    Icon(
                        imageVector = when (screen.route) {
                            Route.Restaurants.route -> Icons.Filled.Fastfood
                            Route.GiftCards.route -> Icons.Filled.CreditCard
                            Route.Cart.route -> Icons.Filled.ShoppingCart
                            Route.Activity.route -> Icons.Filled.Person
                            Route.Account.route -> Icons.Filled.AccountCircle
                            else -> Icons.Filled.Person
                        },
                        modifier = Modifier.size(24.dp),
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = when (screen.route) {
                            Route.Restaurants.route -> "Dining"
                            Route.GiftCards.route -> "Payment" // Changed to singular "Payment" like Uber
                            else -> screen.title
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onTabSelected(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                alwaysShowLabel = true
            )
        }
    }
}
