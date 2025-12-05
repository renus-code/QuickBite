package com.quickbite.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun RedeemGiftCardScreen(navController: NavHostController, userVM: UserViewModel) {
    var giftCardCode by remember { mutableStateOf("") }
    
    val message by userVM.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar when message changes
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            userVM.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Redeem Gift Card",
                canNavigateBack = true,
                navigateUp = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Redeem Gift Card",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Redeem Gift Card",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter the code from your gift card to add it to your balance.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = giftCardCode,
                onValueChange = { giftCardCode = it },
                label = { Text("Gift Card Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                     userVM.redeemGiftCard(giftCardCode.trim())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = giftCardCode.trim().isNotEmpty() // Added trim() logic
            ) {
                Text("Redeem")
            }
        }
    }
}
