package com.quickbite.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun ServicesScreen(navController: NavHostController, userVM: UserViewModel) {
    val services = listOf(
        "Catering" to Icons.Default.RestaurantMenu,
        "Delivery" to Icons.Default.LocalShipping,
        "Event Orders" to Icons.Default.Event,
        "Gift Cards" to Icons.Default.CardGiftcard
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(services) { (service, icon) ->
            ServiceCard(service, icon, navController = navController, onClick = { serviceName ->
                navController.navigate("serviceDetail/" + serviceName)
            })
        }
    }
}

@Composable
fun ServiceCard(serviceName: String, icon: ImageVector, navController: NavHostController, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(serviceName) }),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$serviceName icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = serviceName,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
