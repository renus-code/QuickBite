package com.quickbite.app.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.quickbite.app.components.BottomNavigationBar
import com.quickbite.app.navigation.BottomNavGraph
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun MainBottomNavigationScreen(parentNavController: NavHostController, userVM: UserViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        BottomNavGraph(
            navController = navController,
            userVM = userVM,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

