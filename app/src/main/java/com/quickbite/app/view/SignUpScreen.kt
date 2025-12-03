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
fun SignUpScreen(navController: NavController, userVM: UserViewModel) {
    var displayName by remember { mutableStateOf("") }
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
                value = displayName,
                onValueChange = { displayName = it; errorMessage = "" },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    if (displayName.isBlank()) {
                        errorMessage = "Enter a display name"
                    } else if (!email.contains("@") || !email.contains(".")) {
                        errorMessage = "Enter a valid email"
                    } else if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                    } else {
                        scope.launch {
                            val success = userVM.signup(email, password, displayName)
                            if (success) {
                                navController.navigate("signin") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                errorMessage = "User already exists"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Already have an account? Sign In",
                modifier = Modifier.clickable { navController.navigate("signin") },
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
