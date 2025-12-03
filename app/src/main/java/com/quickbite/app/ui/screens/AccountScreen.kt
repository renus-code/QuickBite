package com.quickbite.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun AccountScreen(
    navController: NavHostController,
    userVM: UserViewModel
) {
    val user by userVM.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Account Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Display Name: ${user?.displayName ?: "Not Available"}",
                    fontSize = 18.sp,
                    color = Color(0xFF555555)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: ${user?.email ?: "Not Available"}",
                    fontSize = 18.sp,
                    color = Color(0xFF555555)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("settings") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text("Settings", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userVM.logout() // Clears user and sets isLoggedIn = false
                navController.navigate("signup") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text("Logout", color = Color.White, fontSize = 16.sp)
        }
    }
}
