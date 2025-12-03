package com.quickbite.app.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.quickbite.app.viewmodel.FoodItem
import com.quickbite.app.viewmodel.RestaurantViewModel

@Composable
fun RestaurantsScreen(restaurantVM: RestaurantViewModel = viewModel()) {

    val foodList by restaurantVM.foodItems.collectAsState()
    val error by restaurantVM.error.collectAsState()

    when {
        error != null -> {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        foodList.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(foodList) { food ->
                    FoodCard(food)
                }
            }
        }
    }
}

@Composable
fun FoodCard(food: FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = food.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "$${food.price}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
