package com.quickbite.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.model.FoodItem
import com.quickbite.app.viewmodel.MenuViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    menuVM: MenuViewModel,
    restaurantName: String,
    onBack: () -> Unit
) {
    val foodList by menuVM.filteredFoodItems.collectAsState()
    val searchQuery by menuVM.searchQuery.collectAsState()
    val recentSearches by menuVM.recentSearches.collectAsState()
    val error by menuVM.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                QuickBiteTopAppBar(
                    title = restaurantName, // Show restaurant name
                    canNavigateBack = true,
                    navigateUp = onBack
                )
                SearchBarUI(
                    query = searchQuery,
                    onQueryChange = { menuVM.onSearchQueryChange(it) },
                    onSearch = { menuVM.onSearchTriggered(it) },
                    onClear = { menuVM.onSearchQueryChange("") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Recent searches
                if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
                    RecentSearchesSection(
                        searches = recentSearches,
                        onSearchClick = { query ->
                            menuVM.onSearchQueryChange(query)
                            menuVM.onSearchTriggered(query)
                        },
                        onClearHistory = { menuVM.clearRecentSearches() }
                    )
                    Divider()
                }

                // Content
                when {
                    error != null -> {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    foodList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (searchQuery.isNotEmpty()) {
                                Text("No items found for \"$searchQuery\"")
                            } else {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = foodList,
                                key = { it.id }
                            ) { food ->
                                FoodCard(
                                    food = food,
                                    onAddToCart = {
                                        menuVM.addToCart(food)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("${food.name} added to cart")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SearchBarUI(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search food...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
fun RecentSearchesSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = onClearHistory) {
                Text("Clear")
            }
        }

        searches.forEach { term ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick(term) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = term,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun FoodCard(food: FoodItem, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = food.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$${food.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(onClick = onAddToCart) {
                Text("Add Cart")
            }
        }
    }
}
