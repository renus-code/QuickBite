package com.quickbite.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.quickbite.app.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    // All bottom nav items
    val items = listOf(
        Screen.Restaurants,
        Screen.Services,
        Screen.Activity,
        Screen.Account
    )

    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen.route) {
                            "restaurants" -> Icons.Filled.Fastfood
                            "services" -> Icons.Filled.ListAlt
                            "activity" -> Icons.Filled.Person
                            "account" -> Icons.Filled.AccountCircle
                            else -> Icons.Filled.Person
                        },
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Blue
                )
            )
        }
    }
}
