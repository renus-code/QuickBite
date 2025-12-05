package com.quickbite.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.model.Order
import com.quickbite.app.viewmodel.RestaurantViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityScreen(restaurantVM: RestaurantViewModel) {
    val recentOrders by restaurantVM.recentOrders.collectAsState()

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "User Activity", // Reverted to "User Activity"
                canNavigateBack = false
            )
        }
    ) { innerPadding ->
        if (recentOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent activity.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentOrders) { order ->
                    ActivityCard(order)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(order: Order) {
    // Date formatter
    val formatter = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault())
    val formattedDate = formatter.format(Date(order.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Order #${order.orderId}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Items: ${order.items.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Total: $${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(
                        imageVector = Icons.Default.CheckCircle, // Assuming all are completed for now
                        contentDescription = order.status,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${order.status} on $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
