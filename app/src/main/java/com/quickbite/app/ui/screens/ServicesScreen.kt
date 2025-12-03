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
fun ServicesScreen(navController: NavHostController, userVM: UserViewModel) {
    val services = listOf("Catering", "Delivery", "Event Orders", "Gift Cards")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(services) { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE3F2FD))
                ) {
                    Text(
                        text = service,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
