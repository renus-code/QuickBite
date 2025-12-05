package com.quickbite.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.quickbite.app.data.AppDatabase
import com.quickbite.app.data.GiftCardRepository
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.data.UserRepository
import com.quickbite.app.navigation.NavGraph
import com.quickbite.app.ui.theme.QuickBiteTheme
import com.quickbite.app.viewmodel.UserViewModel
import com.quickbite.app.viewmodel.UserViewModelFactory
import com.quickbite.app.viewmodel.RestaurantViewModel
import com.quickbite.app.viewmodel.RestaurantViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Database and Repositories
            val database = AppDatabase.getDatabase(applicationContext)
            val userRepository = UserRepository(database.userDao())
            val giftCardRepository = GiftCardRepository(database.giftCardDao())
            val orderRepository = OrderRepository(database.orderDao())

            // ViewModel Factories
            val userViewModelFactory = UserViewModelFactory(userRepository, giftCardRepository)
            val restaurantViewModelFactory = RestaurantViewModelFactory(orderRepository)

            // ViewModels
            val userVM: UserViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)
            val restaurantVM: RestaurantViewModel = ViewModelProvider(this, restaurantViewModelFactory).get(RestaurantViewModel::class.java)

            val useDarkTheme by userVM.darkModeEnabled.collectAsState()

            QuickBiteTheme(darkTheme = useDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph(userVM = userVM, restaurantVM = restaurantVM)
                }
            }
        }
    }
}
