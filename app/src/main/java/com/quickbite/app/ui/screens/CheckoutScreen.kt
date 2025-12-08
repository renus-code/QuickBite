package com.quickbite.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.model.Address
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userVM: UserViewModel,
    menuVM: MenuViewModel,
    navController: NavHostController
) {
    val user by userVM.user.collectAsState()
    val cartItems by menuVM.cartItems.collectAsState()

    var selectedAddress by remember { mutableStateOf<Address?>(user?.addresses?.firstOrNull()) }
    var addressExpanded by remember { mutableStateOf(false) }

    // Payment Method State
    val paymentMethods = listOf("Credit Card", "PayPal", "Gift Card Balance")
    var selectedPaymentMethod by remember { mutableStateOf(user?.paymentMethod ?: paymentMethods.first()) }
    var paymentExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Checkout",
                canNavigateBack = true,
                navigateUp = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Order Summary
            Text("Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            cartItems.forEach {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${it.quantity} x ${it.item.name}")
                    Text("$%.2f".format(it.item.price * it.quantity))
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Address Selection
            Spacer(modifier = Modifier.height(16.dp))
            Text("Shipping Address", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(expanded = addressExpanded, onExpandedChange = { addressExpanded = !addressExpanded }) {
                OutlinedTextField(
                    value = selectedAddress?.toDisplayString() ?: "Select an address",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = addressExpanded, onDismissRequest = { addressExpanded = false }) {
                    user?.addresses?.forEach { address ->
                        DropdownMenuItem(
                            text = { Text(address.toDisplayString()) },
                            onClick = {
                                selectedAddress = address
                                addressExpanded = false
                            }
                        )
                    }
                }
            }
            if (user?.addresses.isNullOrEmpty()) {
                Text("No addresses found. Please add one in your account settings.")
            }

            // Payment Method
            Spacer(modifier = Modifier.height(16.dp))
            Text("Payment Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            // Gift Card Balance Display
            Text("Gift Card Balance: $%.2f".format(user?.balance ?: 0.0), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = paymentExpanded, onExpandedChange = { paymentExpanded = !paymentExpanded }) {
                OutlinedTextField(
                    value = selectedPaymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = paymentExpanded, onDismissRequest = { paymentExpanded = false }) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                selectedPaymentMethod = method
                                paymentExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm Button
            Button(
                onClick = {
                    if (selectedAddress != null) {
                        menuVM.placeOrder(selectedAddress!!, selectedPaymentMethod)
                        navController.popBackStack("cart_home", inclusive = false) 
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = selectedAddress != null
            ) {
                Text("Confirm & Pay")
            }
        }
    }
}
