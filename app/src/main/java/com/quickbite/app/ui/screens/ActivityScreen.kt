package com.quickbite.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.OrderFilter

@Composable
fun ActivityScreen(menuVM: MenuViewModel, navController: NavHostController) {
    val orders by menuVM.orders.collectAsState()
    val filter by menuVM.orderFilter.collectAsState()

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "My Activity",
                canNavigateBack = false
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = filter.ordinal) {
                OrderFilter.values().forEach { filterOption ->
                    Tab(
                        selected = filter == filterOption,
                        onClick = { menuVM.setOrderFilter(filterOption) },
                        text = { Text(filterOption.name.lowercase().replaceFirstChar { it.titlecase() }) }
                    )
                }
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(orders) { order ->
                    ListItem(
                        headlineContent = { Text("Order #${order.orderId}") },
                        supportingContent = { Text("Status: ${order.status}") },
                        modifier = Modifier.clickable { navController.navigate("order_detail/${order.orderId}") }
                    )
                    Divider()
                }
            }
        }
    }
}
