package com.quickbite.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickbite.app.ui.screens.AccountScreen
import com.quickbite.app.ui.screens.SettingsScreen
import com.quickbite.app.ui.view.MainBottomNavigationScreen
import com.quickbite.app.ui.view.SignInScreen
import com.quickbite.app.ui.view.SignUpScreen
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    userVM: UserViewModel = viewModel()
) {
    val isLoggedIn by userVM.isLoggedIn.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = "signup"
    ) {
        composable("signin") {
            SignInScreen(navController = navController, userVM = userVM)
        }
        composable("signup") {
            SignUpScreen(navController = navController, userVM = userVM)
        }
        composable("home") {
            MainBottomNavigationScreen(parentNavController = navController, userVM = userVM)
        }
        composable("account") {
            AccountScreen(navController = navController, userVM = userVM)
        }
        composable("settings") {
            SettingsScreen(navController = navController, userVM = userVM)
        }
    }
}
