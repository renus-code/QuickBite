package com.quickbite.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun ActivityScreen(navController: NavHostController, userVM: UserViewModel) {
    val activities = listOf("Order #123 Completed", "Order #124 Pending", "Order #125 Cancelled")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(activities) { activity ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFFFFF3E0))
                ) {
                    Text(
                        text = activity,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
