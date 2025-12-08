
package com.quickbite.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.quickbite.app.data.AppDatabase
import com.quickbite.app.data.GiftCardRepository
import com.quickbite.app.data.OrderRepository
import com.quickbite.app.data.UserRepository
import com.quickbite.app.navigation.NavGraph
import com.quickbite.app.ui.theme.QuickBiteTheme
import com.quickbite.app.util.SettingsManager
import com.quickbite.app.viewmodel.MenuViewModel
import com.quickbite.app.viewmodel.MenuViewModelFactory
import com.quickbite.app.viewmodel.UserViewModel
import com.quickbite.app.viewmodel.UserViewModelFactory
import com.quickbite.app.viewmodel.RestaurantViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsManager = SettingsManager(applicationContext)
            val database = AppDatabase.getDatabase(applicationContext)
            val userRepository = UserRepository(database.userDao())
            val giftCardRepository = GiftCardRepository(database.giftCardDao())
            val orderRepository = OrderRepository(database.orderDao())

            val userViewModelFactory = UserViewModelFactory(userRepository, giftCardRepository, settingsManager)
            val menuViewModelFactory = MenuViewModelFactory(orderRepository, settingsManager)

            val userVM: UserViewModel = ViewModelProvider(this, userViewModelFactory)
                .get(UserViewModel::class.java)
            val menuVM: MenuViewModel = ViewModelProvider(this, menuViewModelFactory)
                .get(MenuViewModel::class.java)

            val restaurantVM: RestaurantViewModel = viewModel()

            val useDarkTheme by userVM.darkModeEnabled.collectAsState()

            QuickBiteTheme(darkTheme = useDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph(
                        navController = rememberNavController(),
                        userVM = userVM,
                        restaurantVM = restaurantVM,
                        menuVM = menuVM
                    )
                }
            }
        }
    }
}
