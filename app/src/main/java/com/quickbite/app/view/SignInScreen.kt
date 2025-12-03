package com.quickbite.app.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.quickbite.app.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController, userVM: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = "" },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = "" },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!email.contains("@") || !email.contains(".")) {
                        errorMessage = "Enter valid email"
                    } else if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                    } else {
                        val success = userVM.login(email, password)
                        if (success) {
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            errorMessage = "Invalid email or password"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Don't have an account? Sign Up",
                modifier = Modifier.clickable { navController.navigate("signup") },
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
