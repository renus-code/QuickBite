package com.quickbite.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.UserViewModel

@SuppressLint("DefaultLocale")
@Composable
fun PaymentsScreen(navController: NavController, userVM: UserViewModel) {
    val user by userVM.user.collectAsState()

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "Wallet",
                canNavigateBack = false
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Balance Card (Real Data)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "QuickBite Cash",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", user?.balance ?: 0.0)}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.clickable { 
                                navController.navigate("gift_card_landing") 
                            }
                        ) {
                            Text(
                                text = "+ Add Funds",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // 2. Payment Methods Section
            item {
                Column {
                    Text(
                        "Payment Methods",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Show Saved Method if exists
                    if (!user?.paymentMethod.isNullOrEmpty()) {
                         PaymentOptionItem(
                            icon = Icons.Default.CreditCard,
                            title = "Saved Method",
                            subtitle = user?.paymentMethod,
                            onClick = { /* Manage payment */ }
                        )
                         Divider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                         )
                    }

                    // Standard Options
                    PaymentOptionItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Interac",
                        subtitle = "Direct bank transfer",
                        onClick = { /* Select Interac logic */ }
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    PaymentOptionItem(
                        icon = Icons.Default.Add,
                        title = "Add Payment Method",
                        subtitle = null,
                        isAction = true,
                        onClick = { /* Open Add Method Modal */ }
                    )
                }
            }

            // 3. Vouchers / Gift Cards Section
            item {
                Column {
                    Text(
                        "Vouchers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    PaymentOptionItem(
                        icon = Icons.Default.CardGiftcard,
                        title = "Gift Cards",
                        subtitle = "Buy or Redeem",
                        onClick = { navController.navigate("gift_card_landing") }
                    )
                }
            }

            // 4. History Section
            item {
                Column {
                    Text(
                        "Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    PaymentOptionItem(
                        icon = Icons.Default.History,
                        title = "Past Transactions",
                        subtitle = "View order history",
                        onClick = { navController.navigate("activity") }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun PaymentOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    isAction: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isAction) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isAction) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
