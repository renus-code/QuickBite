package com.quickbite.app.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.OrderFilter
import java.util.*
import java.text.SimpleDateFormat

@Composable
fun ActivityScreen(menuVM: MenuViewModel, navController: NavHostController) {
    // Refresh user context when entering screen
    LaunchedEffect(Unit) {
        menuVM.refreshUser()
    }

    val orders by menuVM.orders.collectAsState()
    val filter by menuVM.orderFilter.collectAsState()
    val dateRange by menuVM.dateRange.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "My Activity",
                canNavigateBack = false
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Only show tabs if date range is NOT active
            if (dateRange == null) {
                TabRow(selectedTabIndex = filter.ordinal) {
                    OrderFilter.values().forEach { filterOption ->
                        Tab(
                            selected = filter == filterOption,
                            onClick = { menuVM.setOrderFilter(filterOption) },
                            text = { Text(filterOption.name.lowercase().replaceFirstChar { it.titlecase() }) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dateRange != null) {
                    TextButton(onClick = { menuVM.clearDateRange() }) {
                        Text("Clear Dates")
                    }
                }
                IconButton(onClick = { showDateRangePicker(context, menuVM) }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date Range")
                }
            }

            if (dateRange != null) {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                Text(
                    text = "Showing orders from ${dateFormat.format(Date(dateRange!!.first))} to ${dateFormat.format(Date(dateRange!!.second))}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(orders) { order ->
                    ListItem(
                        headlineContent = { Text("Order #${order.orderId}") },
                        supportingContent = {
                            Column {
                                Text("Status: ${order.status}")
                                Text(
                                    "Date: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(order.timestamp))}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        trailingContent = {
                            Button(onClick = {
                                // Logic to re-order: navigate to order detail or add items to cart
                                navController.navigate("order_detail/${order.orderId}")
                            }) {
                                Text("Re-order")
                            }
                        },
                        modifier = Modifier.clickable { navController.navigate("order_detail/${order.orderId}") }
                    )
                    Divider()
                }
            }
        }
    }
}

fun showDateRangePicker(context: Context, menuVM: MenuViewModel) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val startDatePicker = DatePickerDialog(
        context,
        { _, startYear, startMonth, startDay ->
            val startCal = Calendar.getInstance()
            startCal.set(startYear, startMonth, startDay, 0, 0, 0)
            val startDate = startCal.timeInMillis

            val endDatePicker = DatePickerDialog(
                context,
                { _, endYear, endMonth, endDay ->
                    val endCal = Calendar.getInstance()
                    endCal.set(endYear, endMonth, endDay, 23, 59, 59)
                    val endDate = endCal.timeInMillis
                    menuVM.setDateRange(startDate, endDate)
                },
                year, month, day
            )
            endDatePicker.setTitle("Select End Date")
            // Ensure end date picker doesn't start before start date
            endDatePicker.datePicker.minDate = startDate
            endDatePicker.show()
        },
        year, month, day
    )
    startDatePicker.setTitle("Select Start Date")
    startDatePicker.show()
}
