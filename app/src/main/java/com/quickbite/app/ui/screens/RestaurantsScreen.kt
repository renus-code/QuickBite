package com.quickbite.app.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.quickbite.app.model.Restaurant
import com.quickbite.app.viewmodel.RestaurantViewModel
import com.quickbite.app.components.QuickBiteTopAppBar
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

// Random images for restaurants
val restaurantImages = listOf(
    "https://res.cloudinary.com/the-infatuation/image/upload/c_fill,w_3840,ar_4:3,g_center,f_auto/images/COQODAQ_FoodAmbiance_KatePrevite_NYC_00001_pzptlj",
    "https://static01.nyt.com/images/2024/04/10/multimedia/10best-restaurants-chicago13-hpqj/08best-restaurants-chicago13-hpqj-videoSixteenByNine3000.jpg",
    "https://d1l57x9nwbbkz.cloudfront.net/files/s3fs-public/styles/article_masthead/public/2025-08/restaurant-pearl-morissette-jordan-station-ontario.jpg.webp",
    "https://business.yelp.com/wp-content/uploads/2025/03/yelp-for-restaurants-home.webp",
    "https://images.tastet.ca/_/rs:fit:1080:720:false:0/plain/local:///2025/02/tastet-molenne-35-websize.jpg@jpg",
    "https://winecountry-media.s3.amazonaws.com/wp-content/uploads/sites/4/2024/07/11110433/shutterstock_1678594945-1880x880-1.jpg",
    "https://www.claridges.co.uk/siteassets/restaurants--bars/claridges-restaurants-and-bars-listing-1920x720artboard-1.jpg",
    "https://www.aixenprovencetourism.com/wp-content/uploads/2013/07/ou_manger-1920x1080.jpg",
    "https://cdn.sanity.io/images/nxpteyfv/goguides/5844b6e6a99b4d955c962017adb0766fa35f1b79-1600x1066.jpg",
    "https://www.oland.se/sites/cb_oland/files/styles/slide_large/public/CM103931.jpg.webp"
)

@Composable
fun RestaurantsScreen(
    navController: NavController,
    restaurantVM: RestaurantViewModel,
    onRestaurantClick: ((String) -> Unit)? = null
) {
    val searchQuery by restaurantVM.searchQuery.collectAsState()
    val filteredList by restaurantVM.filteredRestaurants.collectAsState()
    val error by restaurantVM.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            Column {
                QuickBiteTopAppBar(
                    title = "Explore Restaurants",
                    canNavigateBack = false
                )
                RestaurantSearchBar(
                    query = searchQuery,
                    onQueryChange = { restaurantVM.onSearchQueryChange(it) },
                    onClear = { restaurantVM.onSearchQueryChange("") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    }
                }
                filteredList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredList,
                            key = { it.restaurantName + (it.restaurantAddress ?: "") }
                        ) { restaurant ->
                            RestaurantCard(restaurant = restaurant) {
                                onRestaurantClick?.invoke(restaurant.restaurantName ?: "Unknown")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageUrl = remember(restaurant.restaurantName + (restaurant.restaurantAddress ?: "")) {
        restaurantImages.random()
    }
    val placeholderUrl =
        "https://moltaqarestaurant.ca/wp-content/uploads/2023/10/wildlight_opening-menu.jpg"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = restaurant.restaurantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    AsyncImage(
                        model = placeholderUrl,
                        contentDescription = "Error placeholder",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Log.e("ImageLoadError", "Failed to load image, showing placeholder: $imageUrl")
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = restaurant.restaurantName ?: "Unknown Restaurant",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = restaurant.restaurantAddress ?: "No Address Found",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Category: ${restaurant.category ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun RestaurantSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search restaurants...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search icon")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
//        colors = TextFieldDefaults.outlinedTextFieldColors(
//            focusedBorderColor = MaterialTheme.colorScheme.primary,
//            unfocusedBorderColor = Color.Gray,
//            cursorColor = MaterialTheme.colorScheme.primary,
//            containerColor = MaterialTheme.colorScheme.surface
//        )
    )
}

