package com.quickbite.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.MenuViewModel

@Composable
fun OrderDetailScreen(
    orderId: Int,
    menuVM: MenuViewModel,
    navController: NavHostController,
    onReorder: () -> Unit = {}
) {
    val order by menuVM.getOrderById(orderId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Order Details",
                canNavigateBack = true,
                navigateUp = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        order?.let { currentOrder ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("Order #${currentOrder.orderId}", style = MaterialTheme.typography.titleLarge)
                Text("Placed on: ${java.text.SimpleDateFormat.getDateTimeInstance().format(currentOrder.timestamp)}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Items:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                currentOrder.items.forEach { item ->
                    Text(item)
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total:", fontWeight = FontWeight.Bold)
                    Text("$%.2f".format(currentOrder.totalPrice), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        menuVM.reOrder(currentOrder)
                        onReorder()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Place Same Order Again")
                }
            }
        }
    }
}