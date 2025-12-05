package com.quickbite.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun PurchaseGiftCardScreen(navController: NavHostController, userVM: UserViewModel) {
    var senderName by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val presets = listOf("25", "50", "100", "200")

    // Payment State
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // Success Dialog State
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedCode by remember { mutableStateOf("") }

    val message by userVM.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current 

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            userVM.clearMessage()
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                // When dismissed, go back to previous screen
                showSuccessDialog = false 
                navController.popBackStack()
            },
            title = { Text("Purchase Successful!") },
            text = {
                Column {
                    Text("You have successfully purchased a gift card.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your Gift Code:", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = generatedCode,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("An email draft has been opened for you to send this code to $recipientName.")
                }
            },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text("Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Purchase Gift Card",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Send a Gift",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Preset Amounts Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Amount",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets) { preset ->
                        SuggestionChip(
                            onClick = { amount = preset },
                            label = { Text("$$preset") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (amount == preset) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (amount == preset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Recipient Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                label = { Text("Recipient's Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = recipientEmail,
                onValueChange = { recipientEmail = it },
                label = { Text("Recipient's Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = senderName,
                onValueChange = { senderName = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Payment Details Section
            Text(
                text = "Payment Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16) cardNumber = it },
                label = { Text("Card Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { if (it.length <= 5) expiryDate = it },
                    label = { Text("MM/YY") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it },
                    label = { Text("CVV") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null) {
                        // Generate Code
                        val newCode = "QBGIFT-${System.currentTimeMillis().toString().takeLast(4)}"
                        generatedCode = newCode
                        
                        // 1. Update ViewModel
                        userVM.purchaseGiftCard(
                            amount = amountValue,
                            senderName = senderName,
                            recipientName = recipientName,
                            recipientEmail = recipientEmail,
                            customCode = newCode // PASS THE CODE HERE
                        )

                        // 2. Send Email Intent
                        val emailSubject = "QuickBite Gift Card from $senderName"
                        val emailBody = """
                            Hi $recipientName,
                            
                            $senderName has sent you a QuickBite Gift Card worth $$amountValue!
                            
                            You can redeem this using your email address or a code in the app.
                            
                            Code: $newCode
                            
                            Enjoy your meal!
                            The QuickBite Team
                        """.trimIndent()

                        val emailIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        
                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send Gift Card via..."))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                        }
                        
                        // 3. Show Success Dialog (Don't popBackStack yet)
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = amount.isNotBlank() && 
                          recipientEmail.contains("@") && 
                          cardNumber.length >= 10 && 
                          expiryDate.length >= 4 && 
                          cvv.length >= 3
            ) {
                Text("Pay & Send Gift Card")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
