
package com.quickbite.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.model.CartItem
import com.quickbite.app.viewmodel.MenuViewModel

@Composable
fun CartScreen(
    menuVM: MenuViewModel,
    navController: NavHostController? = null,
    isBottomNav: Boolean = false
) {
    val cartItems by menuVM.cartItems.collectAsState()
    val showDialog by menuVM.showOrderStatusDialog.collectAsState()
    val statusMessage by menuVM.orderStatusMessage.collectAsState()

    val subtotal = cartItems.sumOf { it.item.price * it.quantity }
    val taxRate = 0.13
    val taxes = subtotal * taxRate
    val total = subtotal + taxes

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Order Status") },
            text = { Text(statusMessage) },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Cart",
                canNavigateBack = !isBottomNav,
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
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(cartItems, key = { it.item.id }) { cartItem ->
                        CartItemRow(cartItem = cartItem, menuVM = menuVM)
                        Divider()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pricing Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontWeight = FontWeight.Bold)
                        Text("$${"%.2f".format(subtotal)}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Taxes (13%)")
                        Text("$${"%.2f".format(taxes)}")
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text("$${"%.2f".format(total)}", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { menuVM.placeOrder() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text("Place Order")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, menuVM: MenuViewModel) {
    key(cartItem.item.id) { // This key ensures the item is uniquely identified and tracked
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.item.name, style = MaterialTheme.typography.bodyLarge)
                if (cartItem.quantity > 1) {
                    Text(
                        text = "${cartItem.quantity} @ $${String.format("%.2f", cartItem.item.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "$${String.format("%.2f", cartItem.item.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { menuVM.decreaseQuantity(cartItem.item) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                }
                Text(cartItem.quantity.toString(), fontWeight = FontWeight.Bold)
                IconButton(onClick = { menuVM.increaseQuantity(cartItem.item) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                }
            }
        }
    }
}
