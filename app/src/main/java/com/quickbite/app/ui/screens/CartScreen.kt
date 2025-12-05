package com.quickbite.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.FoodItem
import com.quickbite.app.viewmodel.RestaurantViewModel

@Composable
fun CartScreen(
    restaurantVM: RestaurantViewModel = viewModel(),
    navController: NavHostController? = null,
    isBottomNav: Boolean = false // Added parameter
) {
    val cartItems by restaurantVM.cartItems.collectAsState()
    val showDialog by restaurantVM.showOrderStatusDialog.collectAsState()
    val statusMessage by restaurantVM.orderStatusMessage.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Don't allow dismissal */ },
            title = { Text("Order Status") },
            text = { Text(statusMessage) },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Cart",
                canNavigateBack = !isBottomNav, // Hide back button if on bottom nav
                navigateUp = { navController?.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your cart is empty.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { item ->
                        CartItemRow(item)
                        HorizontalDivider()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { restaurantVM.placeOrder() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Place Order")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: FoodItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
        Text(text = "$${item.price}", style = MaterialTheme.typography.bodyLarge)
    }
}
